package com.sporksoft.kiddom.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailedDescription(
        val articleBody: String,
        val license: String,
        val url: String) : Parcelable