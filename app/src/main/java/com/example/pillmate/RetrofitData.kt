package com.example.pillmate

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("wakeUp")
    val wakeUp: String,
    @SerializedName("morning")
    val morning: String,
    @SerializedName("lunch")
    val lunch: String,
    @SerializedName("dinner")
    val dinner: String,
    @SerializedName("bed")
    val bed: String,
    @SerializedName("diseases")
    val diseases: List<DiseaseData>,
    @SerializedName("roles")
    val roles: List<String>
)

data class DiseaseData(
    @SerializedName("disease")
    val disease: String,
    @SerializedName("startDate")
    val startDate: String
)

data class SignUpResponse(
    @SerializedName("grantType")
    val grantType: String,
    @SerializedName("accessToken")
    val accessToken: String
)