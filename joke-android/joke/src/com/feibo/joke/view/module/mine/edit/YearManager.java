package com.feibo.joke.view.module.mine.edit;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.text.TextUtils;

import com.feibo.joke.app.AppContext;
import com.feibo.joke.app.Constant;
import com.feibo.joke.utils.SPHelper;

import fbcore.log.LogUtil;

public class YearManager {

	private static final String TAG = "AddressDataManager";
	
	private static YearManager manager;
	
	public static YearManager getInstance() {
		if(manager == null) {
			manager = new YearManager();
		}
		return manager;
	}

	public AddressData getAddressData() {
		AddressData addressData = getAddressDataNew();
        if(addressData != null) {
            return addressData;
        }
        return getAddressDataDefault();
	}
	
	// 记载默认的可选择地址的文件
    private AddressData getAddressDataDefault() {
        LogUtil.i(TAG, "getAddressDataDefault");
        try {
            InputStream inputStream = AppContext.getContext().getResources().getAssets().open(Constant.PATH_AREA);
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
	
	// 加载新的可选择地址的文件
    private AddressData getAddressDataNew() {
        LogUtil.i(TAG, "getAddressDataNew");
        
        //TODO  获取地址文件路径
        String selectAddressPath = SPHelper.getAddressFilePath();;
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
        addressData.provinceList = new ArrayList<AddressNameBean>();
        for (int i = 0; i < provinceNodeList.getLength(); i++) {
            Node provinceNode = provinceNodeList.item(i);
            if (provinceNode.getNodeType() == Node.ELEMENT_NODE && provinceNode.getNodeName().equals("dict")) {
                // 省
                Province province = new Province();
                province.cityList = new ArrayList<AddressNameBean>();
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
                                city.areaList = new ArrayList<AddressNameBean>();

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
