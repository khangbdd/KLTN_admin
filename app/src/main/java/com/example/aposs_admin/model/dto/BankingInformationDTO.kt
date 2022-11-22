package com.example.aposs_admin.model.dto

data class BankingInformationDTO(
    var bankName: String,
    var branch: String,
    var receiverName: String,
    var accountNumber: String
){
    fun equalTo(otherBankingInformationDTO: BankingInformationDTO?):Boolean{
        return bankName == otherBankingInformationDTO?.bankName &&
                branch == otherBankingInformationDTO.branch &&
                receiverName == otherBankingInformationDTO.receiverName &&
                accountNumber == otherBankingInformationDTO.accountNumber
    }
}
