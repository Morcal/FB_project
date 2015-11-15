package com.feibo.snacks.model.bean.group;

import com.feibo.snacks.model.bean.CartSuppliers;
import com.feibo.snacks.model.bean.Express;
import com.google.gson.annotations.SerializedName;

public class ExpressDetail {
    
    @SerializedName("cart_suppliers")
    public CartSuppliers cartSuppliers;
    
    @SerializedName("express")
    public Express express;

}
