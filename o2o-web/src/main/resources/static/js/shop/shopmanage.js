$(function () {

    var shopId=getQueryString("shopId");
    var shopInfo="/o2o/shop/shopedit?shopId="+shopId;
    $.ajax({
        url:"/o2o/shop/getshopmanageinfo?shopId="+shopId,
        type : 'GET',
        contentType : false,
        processData : false,
        cache : false
    });
    $('#shopinfo').attr('href',shopInfo);
})
