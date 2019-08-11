一、条件查询所有影院
- 请求：
    设置vo：**CinemaRequestVO**
    请求字段	    字段含义	    是否必填
    brandId	    影院编号	    否,默认为99，全部
    hallType	影厅类型	    否,默认为99，全部
    districtId	行政区编号	否,默认为99，全部
    pageSize	每页条数	    否,默认为12条
    nowPage	    当前页数	    否,默认为第1页

- 响应
    **CinemaVO**:
    “uuid”:         1231,
    “cinemaName”:   “大地影院”,
    “address”:      ”东城区滨河路乙1号雍和航星园74-76号楼”,
    “minimumPrice”: 48.5


二、获取影院列表+查询条件
- 请求
    请求字段	    字段含义	    是否必填
    brandId	    影院编号	    否,默认为99，全部
    hallType	影厅类型	    否,默认为99，全部
    areaId	    行政区编号	否,默认为99，全部

- 响应
    **BrandVO**
    “brandId”:      1,
    “brandName”:    ”全部”,
    “isActive”:     true

    **AreaVO**
    “areaId”:       1,
    “areaName”:     ”全部”,
    “isActive”:     true

    **HallTypeVO**
    “halltypeId”:   1,
    “halltypeName”: ”全部”,
    “isActive”:     true
    

三、获取播放场次
- 请求
    cinemaId	影院编号	

- 响应
    **CinemaInfoVO**
        “cinemaId”:     123594,
        “imgUrl”:       ”cinemas/123.jpg”,
        “cinemaName”:   ”大地影院”,
        “cinemaAdress”: ” 顺义区新顺南大街11号隆华购物中心6F”,
        “cinemaPhone”:  ” 010-89472732”

    **FilmInfoVO**
        “filmId”:       ”10”,
        “filmName”:     ”我不是药神”,
        “filmLength”:   ”117分钟”,
        "filmType":     "国语2D",
        “filmCats”:     ”剧情”,
        “actors”:       ”徐峥,周一围,王传君”,
        “imgAddress”:   ”films/12312312.jpg”
        “filmFields”:   **FilmFieldVO**
        **FilmFieldVO** `影片场次`
            “fieldId”:      1,
            “beginTime”:    ”09:30”,
            “endTime”:      ”11:20”,
            “language”:     ”中文2D”,
            “hallName”:     ”4号厅(激光)”,
            “price”:        ”60”

四、获取场次详细信息
- 请求
    请求字段	    字段含义	    是否必填
    cinemaId	影院编号	    是
    fieldId	    场次编号	    是

- 响应
    **FilmInfoVO** 少actors和**FilmFieldVO**
    
    **CinemaInfoVO**
    
    **HallInfoVO**
        "hallFieldId":  "1",
        "hallName":     "1号VIP厅",
        "price":        48,
        "seatFile":     "halls/4552.json",
        "soldSeats":    "1,2,3,5,12"

