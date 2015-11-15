package com.feibo.snacks.manager.global;

import android.text.TextUtils;

import com.feibo.snacks.app.AppContext;
import com.feibo.snacks.app.DirContext;
import com.feibo.snacks.manager.AbsListHelper;
import com.feibo.snacks.manager.AbsLoadHelper;
import com.feibo.snacks.manager.AbsSubmitHelper;
import com.feibo.snacks.manager.ILoadingListener;
import com.feibo.snacks.model.bean.Address;
import com.feibo.snacks.model.bean.NetResult;
import com.feibo.snacks.model.bean.Response;
import com.feibo.snacks.model.bean.UrlBean;
import com.feibo.snacks.model.dao.DaoListener;
import com.feibo.snacks.model.dao.download.Download;
import com.feibo.snacks.model.dao.SnacksDao;
import com.feibo.snacks.model.dao.cache.BaseDataType;
import com.feibo.snacks.model.dao.cache.DataDiskProvider;
import com.feibo.snacks.util.SPHelper;
import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import fbcore.log.LogUtil;

/**
 * 地址的一些管理操作
 * Created by lidiqing on 15-9-1.
 */
public class AddressManager {
    private static final String TAG = AddressManager.class.getSimpleName();
    private static AddressManager sAddressManager;

    public static AddressManager getInstance() {
        if (sAddressManager == null) {
            sAddressManager = new AddressManager();
        }
        return sAddressManager;
    }

    private final static int ADD_ADDRESS_DEFAULT_INDEX = 0;
    private final static int ADD_ADDRESS_UN_DEFAULT_INDEX = 1;
    private static final String ADDRESS_ADD_CACHE = "address_add";

    private AbsListHelper addressListHelper;            // 地址列表
    private AbsSubmitHelper addAddressHelper;           // 添加地址
    private AbsSubmitHelper deleteAddressHelper;        // 删除地址
    private AbsSubmitHelper setDefaultAddressHelper;    // 设置默认地址

    private AddressManager() {

        addressListHelper = new AbsListHelper(BaseDataType.AddressDataType.ADDRESS_LIST) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                SnacksDao.getAddressList(listener);
            }
        };

        addAddressHelper = new AbsSubmitHelper(BaseDataType.AddressDataType.ADDRESS) {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                Address address = (Address) params;
                SnacksDao.addOrChangeAddress(address, listener);
            }
        };

        deleteAddressHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                long addressId = (Long) params;
                SnacksDao.deleteAddress(addressId, listener);
            }
        };

        setDefaultAddressHelper = new AbsSubmitHelper() {
            @Override
            public void loadData(boolean needCache, Object params, DaoListener listener) {
                long addressId = (Long) params;
                SnacksDao.setAddressDefault(addressId, listener);
            }
        };
    }

    // 设置默认地址
    public void setAddressDefault(long addressId, ILoadingListener listener) {
        setDefaultAddressHelper.setParams(addressId);
        setDefaultAddressHelper.submitData(setDefaultAddressHelper.generateLoadingListener(listener));
    }

    // 删除地址
    public void deleteAddress(final int index, final ILoadingListener listener) {
        Address address = getAddress(index);
        if (address == null) {
            return;
        }

        deleteAddressHelper.setParams(address.id);
        deleteAddressHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                List<Address> list = getAddressList();
                if (index < 0 || index >= list.size()) {
                    return;
                }
                list.remove(index);
                if (listener == null) {
                    return;
                }
                listener.onSuccess();
            }

            @Override
            public void onFail(String failMsg) {
                if (listener == null) {
                    return;
                }
                listener.onFail(failMsg);
            }
        });
    }

    // 添加地址
    public void addAddress(final Address address, final ILoadingListener listener) {
        address.id = 0;
        addAddressHelper.setParams(address);
        addAddressHelper.submitData(new AbsLoadHelper.HelperListener() {
            @Override
            public void onSuccess() {
                address.id = getAddressResult().id;
                LogUtil.i(TAG, "result address:" + address.id + "; " + address.name);
                List<Address> list = getAddressList();
                if (list == null) {
                    list = new ArrayList<Address>();
                    addressListHelper.putData(list);
                }

                //判断是否设置为默认地址并添加到相应位置
                if (address.type == Address.TYPE_DEFAULT || list.size() < 1) {
                    list.add(ADD_ADDRESS_DEFAULT_INDEX, address);
                    isDefaultAddress(address, ADD_ADDRESS_DEFAULT_INDEX);
                } else {
                    list.add(ADD_ADDRESS_UN_DEFAULT_INDEX, address);
                }

                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFail(String failMsg) {
                if (listener != null) {
                    listener.onFail(failMsg);
                }
            }
        });
    }

    // 更新地址
    public void updateAddress(final Address address, final ILoadingListener listener) {
        addAddressHelper.setParams(address);
        addAddressHelper.submitData(addressListHelper.generateLoadingListener(listener));
    }

    // 刷新地址信息
    public void refresh(ILoadingListener listener) {
        addressListHelper.refresh(true, addressListHelper.generateLoadingListener(listener));
    }

    /**
     * 判断是否设置为默认地址，是的话改变设置为默认地址后位置为第一个
     *
     * @param address
     * @param index
     * @return 是否设置了默认地址
     */
    public boolean isDefaultAddress(Address address, int index) {
        if (address.type == Address.TYPE_DEFAULT) {
            for (Address tempAddress : getAddressList()) {
                if (tempAddress.id != address.id && tempAddress.type == Address.TYPE_DEFAULT) {
                    tempAddress.type = Address.TYPE_NORMAL;
                }
            }
            if (index != 0) {
                List<Address> list = getAddressList();
                list.add(0, address);
                list.remove(index + 1);
            }
            return true;
        }
        return false;
    }

    public Address getAddress(int index) {
        List<Address> addressList = getAddressList();
        if (addressList == null || addressList.size() < index) {
            return null;
        }
        return addressList.get(index);
    }

    // 获取新增地址的缓存
    @Deprecated
    public Address getAddressAddCache() {
        String addressStr = DataDiskProvider.getInstance().getCacheFromDiskByKey(ADDRESS_ADD_CACHE);
        if(!TextUtils.isEmpty(addressStr)) {
            return new Gson().fromJson(addressStr, Address.class);
        }
        return null;
    }

    // 清除新增地址的缓存
    @Deprecated
    public void clearAddressAddCache() {
        DataDiskProvider.getInstance().clearCacheFromDiskByKey(ADDRESS_ADD_CACHE);
    }

    // 设置新增地址的缓存
    @Deprecated
    public void setAddressAddCache(Address addressAddCache) {
        String addressStr = new Gson().toJson(addressAddCache);
        DataDiskProvider.getInstance().putStringToDiskByKey(addressStr, ADDRESS_ADD_CACHE);
    }

    public List<Address> getAddressList() {
        return (List<Address>) addressListHelper.getData();
    }

    // 添加地址成功后，后台返回保存好的地址，里面注入了地址id
    public Address getAddressResult() {
        return (Address) addAddressHelper.getData();
    }

    public boolean hasMoreAddress() {
        return addressListHelper.hasMoreData();
    }

    public BaseDataType getListDataType() {
        return BaseDataType.AddressDataType.ADDRESS_LIST;
    }

    // 加载新的可选择的地址信息
    public void loadNewAddress() {
        LogUtil.i(TAG, "loadNewAddress");


        int version = 0;
        // 判断地址文件地址是否存在，如果存在，获取版本号
        String addressFilePath = SPHelper.getAddressFilePath();
        if (!TextUtils.isEmpty(addressFilePath)) {
            version = SPHelper.getAddressFileVersion();
        }

        SnacksDao.getSelectAddress(version, (Response<UrlBean> response) -> {
            if (response.code.equals(NetResult.SUCCESS_CODE)) {
                final UrlBean urlBean = response.data;
                String savePath = DirContext.getInstance().getDir(DirContext.DirEnum.DOWNLOAD).getAbsolutePath();
                Download.download(urlBean.url, savePath, "area.plist", new Download.OnDownloadListener() {
                    @Override
                    public void onSuccess() {
                        SPHelper.setAddressFilePath(savePath + "/area.plist");
                        SPHelper.setAddressFileVersion(urlBean.version);
                    }

                    @Override
                    public void onFail() {
                    }
                });
            }
        });
    }

    // 获取可选择的地址信息
    public AddressData getAddressData() {
        AddressData addressData = getAddressDataNew();
        if(addressData != null) {
            return addressData;
        }
        return getAddressDataDefault();
    }

    // 加载新的可选择地址的文件
    private AddressData getAddressDataNew() {
        LogUtil.i(TAG, "getAddressDataNew");
        String selectAddressPath = SPHelper.getAddressFilePath();
        InputStream inputStream = null;

        if (TextUtils.isEmpty(selectAddressPath) || !new File(selectAddressPath).exists()) {
            return null;
        }
        try {
            inputStream = new FileInputStream(new File(selectAddressPath));
            AddressData addressData = parseAddressFile(inputStream);
            return addressData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 记载默认的可选择地址的文件
    private AddressData getAddressDataDefault() {
        LogUtil.i(TAG, "getAddressDataDefault");
        try {
            InputStream inputStream = AppContext.getContext().getResources().getAssets().open("area.plist");
            AddressData addressData = parseAddressFile(inputStream);
            return addressData;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 解析可选择地址文件
    private AddressData parseAddressFile(InputStream inputStream) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        Element rootElement = document.getDocumentElement();
        NodeList rootElementChildNodeList = rootElement.getChildNodes();
        Node rootNode = null;
        for (int i = 0; i < rootElementChildNodeList.getLength(); i++) {
            Node rootChildNode = rootElementChildNodeList.item(i);
            if (rootChildNode.getNodeType() == Node.ELEMENT_NODE && rootChildNode.getNodeName().equals("dict")) {
                rootNode = rootChildNode;
            }
        }

        NodeList provinceNodeList = rootNode.getChildNodes();
        AddressData addressData = new AddressData();
        addressData.provinceList = new ArrayList<>();
        for (int i = 0; i < provinceNodeList.getLength(); i++) {
            Node provinceNode = provinceNodeList.item(i);
            if (provinceNode.getNodeType() == Node.ELEMENT_NODE && provinceNode.getNodeName().equals("dict")) {
                // 省
                Province province = new Province();
                province.cityList = new ArrayList<>();
                NodeList provinceChildList = provinceNode.getChildNodes();
                for (int ip = 0; ip < provinceChildList.getLength(); ip++) {
                    Node provinceChildNode = provinceChildList.item(ip);
                    if (provinceChildNode.getNodeType() == Node.ELEMENT_NODE && provinceChildNode.getNodeName().equals("key")) {
//                        LogUtil.i(TAG, "province name:" + provinceChildNode.getFirstChild().getNodeValue());
                        province.name = provinceChildNode.getFirstChild().getNodeValue();
                    }

                    if (provinceChildNode.getNodeType() == Node.ELEMENT_NODE && provinceChildNode.getNodeName().equals("dict")) {
                        NodeList cityNodeList = provinceChildNode.getChildNodes();
                        for (int j = 0; j < cityNodeList.getLength(); j++) {
                            Node cityNode = cityNodeList.item(j);
                            if (cityNode.getNodeType() == Node.ELEMENT_NODE && cityNode.getNodeName().equals("dict")) {
                                // 市
                                City city = new City();
                                city.areaList = new ArrayList<>();

                                NodeList cityChildList = cityNode.getChildNodes();
                                for (int jc = 0; jc < cityChildList.getLength(); jc++) {
                                    Node cityChildNode = cityChildList.item(jc);

                                    if (cityChildNode.getNodeType() == Node.ELEMENT_NODE && cityChildNode.getNodeName().equals("key")) {
//                                        LogUtil.i(TAG, "city name:" + cityChildNode.getFirstChild().getNodeValue());
                                        city.name = cityChildNode.getFirstChild().getNodeValue();
                                    }

                                    if (cityChildNode.getNodeType() == Node.ELEMENT_NODE && cityChildNode.getNodeName().equals("array")) {
                                        NodeList areaNodeList = cityChildNode.getChildNodes();

                                        for (int k = 0; k < areaNodeList.getLength(); k++) {
                                            Node areaNode = areaNodeList.item(k);

                                            if (areaNode.getNodeType() == Node.ELEMENT_NODE && areaNode.getNodeName().equals("string")) {
                                                // 区
//                                                LogUtil.i(TAG, "area: " + areaNode.getFirstChild().getNodeValue());
                                                Area area = new Area();
                                                area.name = areaNode.getFirstChild().getNodeValue();
                                                city.areaList.add(area);
                                            }

                                        }
                                    }

                                }
                                province.cityList.add(city);
                            }
                        }
                    }
                }
                addressData.provinceList.add(province);
            }
        }
        return addressData;
    }

    public static class AddressData {
        public List<AddressNameBean> provinceList;
    }

    public static class Province extends AddressNameBean {
        public List<AddressNameBean> cityList;
    }

    public static class City extends AddressNameBean {
        public List<AddressNameBean> areaList;
    }

    public static class Area extends AddressNameBean {
    }

    public static class AddressNameBean {
        public String name;
    }
}
