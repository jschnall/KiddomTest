package com.sporksoft.kiddom.models

data class Result(
        //@id : String,
        //@type : List<String>,
        val description: String?,
        val detailedDescription: DetailedDescription?,
        val name: String?,
        val image: Image?
)