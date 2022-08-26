package com.demo.smileid.sid_sdk.sidNet;

import com.smileidentity.libsmileid.core.idcard.Country;
import com.smileidentity.libsmileid.core.idcard.IdCard;
import com.smileidentity.libsmileid.core.idcard.IdType;

public class IdTypeUtil {

    public static IdCard idCards(String country) {
        switch (country) {
            case Country.ALGERIA:
                return new IdCard.For(Country.ALGERIA)
                        .setCountryCode("233")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ANGOLA:
                return new IdCard.For(Country.ANGOLA)
                        .setCountryCode("244")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BENIN:
                return new IdCard.For(Country.BENIN)
                        .setCountryCode("229")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BOTSWANA:
                return new IdCard.For(Country.BOTSWANA)
                        .setCountryCode("267")
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BURKINA_FASO:
                return new IdCard.For(Country.BURKINA_FASO)
                        .setCountryCode("226")
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BURUNDI:
                return new IdCard.For(Country.BURUNDI)
                        .setCountryCode("257")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.CAMEROON:
                return new IdCard.For(Country.CAMEROON)
                        .setCountryCode("237")
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.CABO_VERDE:
                return new IdCard.For(Country.CABO_VERDE)
                        .setCountryCode("238")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.CHAD:
                return new IdCard.For(Country.CHAD)
                        .setCountryCode("235")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.COMOROS:
                return new IdCard.For(Country.COMOROS)
                        .setCountryCode("269")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.CONGO:
                return new IdCard.For(Country.CONGO)
                        .setCountryCode("242")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.IVORY_COAST:
                return new IdCard.For(Country.IVORY_COAST)
                        .setCountryCode("225")
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .create();

            case Country.CONGO_DR:
                return new IdCard.For(Country.CONGO_DR)
                        .setCountryCode("243")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .create();

            case Country.DJIBOUTI:
                return new IdCard.For(Country.DJIBOUTI)
                        .setCountryCode("253")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.EGYPT:
                return new IdCard.For(Country.EGYPT)
                        .setCountryCode("20")
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.EQ_GUINEA:
                return new IdCard.For(Country.EQ_GUINEA)
                        .setCountryCode("240")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ERITREA:
                return new IdCard.For(Country.ERITREA)
                        .setCountryCode("291")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ETHIOPIA:
                return new IdCard.For(Country.ETHIOPIA)
                        .setCountryCode("251")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.GABON:
                return new IdCard.For(Country.GABON)
                        .setCountryCode("241")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.GAMBIA:
                return new IdCard.For(Country.GAMBIA)
                        .setCountryCode("233")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.GHANA:
                return new IdCard.For(Country.GHANA)
                        .setCountryCode("233")
                        /*.addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.NATIONAL_ID_NON_CITIZEN.replace("_", " "))*/
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.SSNIT)
                        .addCard(IdType.VOTER_ID.replace("_", " "))
                        .addCard(IdType.NEW_VOTER_ID.replace("_", " "))
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.GUINEA:
                return new IdCard.For(Country.GUINEA)
                        .setCountryCode("224")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.GUINEA_BISSAU:
                return new IdCard.For(Country.GUINEA_BISSAU)
                        .setCountryCode("245")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.KENYA:
                return new IdCard.For(Country.KENYA)
                        .setCountryCode("254")
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.ALIEN_CARD.replace("_", " "))
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
//                        .addCard(IdType.REFUGEE_CARD.replace("_", " "))
                        .create();

            case Country.LESOTHO:
                return new IdCard.For(Country.LESOTHO)
                        .setCountryCode("266")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.LIBERIA:
                return new IdCard.For(Country.LIBERIA)
                        .setCountryCode("231")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.LIBYA:
                return new IdCard.For(Country.LIBYA)
                        .setCountryCode("218")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MADAGASCAR:
                return new IdCard.For(Country.MADAGASCAR)
                        .setCountryCode("261")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MAlAWI:
                return new IdCard.For(Country.MAlAWI)
                        .setCountryCode("265")
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MAlI:
                return new IdCard.For(Country.MAlI)
                        .setCountryCode("223")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MAURITIUS:
                return new IdCard.For(Country.MAURITIUS)
                        .setCountryCode("230")
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MOROCCO:
                return new IdCard.For(Country.MOROCCO)
                        .setCountryCode("212")
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MOZAMBIQUE:
                return new IdCard.For(Country.MOZAMBIQUE)
                        .setCountryCode("258")
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.NAMIBIA:
                return new IdCard.For(Country.NAMIBIA)
                        .setCountryCode("264")
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.NIGER:
                return new IdCard.For(Country.NIGER)
                        .setCountryCode("227")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.NIGERIA:
                return new IdCard.For(Country.NIGERIA)
                        .setCountryCode("234")
                        /*.addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)*/
                        .addCard(IdType.BANK_ACCOUNT.replace("_", " "))
                        .addCard(IdType.BVN)
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.NIN)
                        .addCard(IdType.NIN_SLIP.replace("_", " "))
//                        .addCard(IdType.PHONE_NUMBER.replace("_", " "))
                        .addCard(IdType.VOTER_ID.replace("_", " "))
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.RWANDA:
                return new IdCard.For(Country.RWANDA)
                        .setCountryCode("250")
                        .addCard(IdType.RWANDA_CARD.replace("_", " "))
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SAO_TOME_AND_PRINCIPE:
                return new IdCard.For(Country.SAO_TOME_AND_PRINCIPE)
                        .setCountryCode("239")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SENEGAL:
                return new IdCard.For(Country.SENEGAL)
                        .setCountryCode("234")
                        .addCard(IdType.ECOWAS_ID.replace("_", " "))
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SEYCHELLES:
                return new IdCard.For(Country.SEYCHELLES)
                        .setCountryCode("248")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SIERRA_LEONE:
                return new IdCard.For(Country.SIERRA_LEONE)
                        .setCountryCode("232")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SOMALIA:
                return new IdCard.For(Country.SOMALIA)
                        .setCountryCode("252")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SOUTH_AFRICA:
                return new IdCard.For(Country.SOUTH_AFRICA)
                        .setCountryCode("27")
                        /*.addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.TRAVEL_DOCUMENT.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.NATIONAL_ID_DOC.replace("_", " "))*/
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.NATIONAL_ID_NO_PHOTO.replace("_", " "))
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SOUTH_SUDAN:
                return new IdCard.For(Country.SOUTH_SUDAN)
                        .setCountryCode("211")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SUDAN:
                return new IdCard.For(Country.SUDAN)
                        .setCountryCode("249")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.TOGO:
                return new IdCard.For(Country.TOGO)
                        .setCountryCode("228")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.TUNISIA:
                return new IdCard.For(Country.TUNISIA)
                        .setCountryCode("216")
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.TANZANIA:
                return new IdCard.For(Country.TANZANIA)
                        .setCountryCode("255")
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.CITIZEN_ID.replace("_", " "))
                        .addCard(IdType.VOTER_ID.replace("_", " "))
                        .addCard(IdType.STUDENT_ID.replace("_", " "))
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.UGANDA:
                return new IdCard.For(Country.UGANDA)
                        .setCountryCode("256")
                        /*.addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.NATIONAL_ID.replace("_", " "))*/
                        .addCard(IdType.NATIONAL_ID_NO_PHOTO.replace("_", " "))
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ZAMBIA:
                return new IdCard.For(Country.ZAMBIA)
                        .setCountryCode("260")
                        .addCard(IdType.DRIVER_LICENSE.replace("_", " "))
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ZIMBABWE:
                return new IdCard.For(Country.ZIMBABWE)
                        .setCountryCode("263")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BAHAMAS:
                return new IdCard.For(Country.BAHAMAS)
                        .setCountryCode("1")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BERMUDA:
                return new IdCard.For(Country.BERMUDA)
                        .setCountryCode("1")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.CANADA:
                return new IdCard.For(Country.CANADA)
                        .setCountryCode("1")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.JAMAICA:
                return new IdCard.For(Country.JAMAICA)
                        .setCountryCode("1")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.US:
                return new IdCard.For(Country.US)
                        .setCountryCode("1")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ALBANIA:
                return new IdCard.For(Country.ALBANIA)
                        .setCountryCode("355")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ANDORRA:
                return new IdCard.For(Country.ANDORRA)
                        .setCountryCode("376")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.AUSTRIA:
                return new IdCard.For(Country.AUSTRIA)
                        .setCountryCode("43")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BELARUS:
                return new IdCard.For(Country.BELARUS)
                        .setCountryCode("375")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BELGIUM:
                return new IdCard.For(Country.BELGIUM)
                        .setCountryCode("32")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BOSNIA_AND_HERZEGOVINA:
                return new IdCard.For(Country.BOSNIA_AND_HERZEGOVINA)
                        .setCountryCode("387")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.BULGARIA:
                return new IdCard.For(Country.BULGARIA)
                        .setCountryCode("359")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.CROATIA:
                return new IdCard.For(Country.CROATIA)
                        .setCountryCode("385")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.CZECHIA:
                return new IdCard.For(Country.CZECHIA)
                        .setCountryCode("420")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.DENMARK:
                return new IdCard.For(Country.DENMARK)
                        .setCountryCode("45")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ESTONIA:
                return new IdCard.For(Country.ESTONIA)
                        .setCountryCode("372")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.FINLAND:
                return new IdCard.For(Country.FINLAND)
                        .setCountryCode("358")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.FRANCE:
                return new IdCard.For(Country.FRANCE)
                        .setCountryCode("33")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.GERMANY:
                return new IdCard.For(Country.GERMANY)
                        .setCountryCode("49")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.GREECE:
                return new IdCard.For(Country.GREECE)
                        .setCountryCode("30")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.HOLY_SEE:
                return new IdCard.For(Country.HOLY_SEE)
                        .setCountryCode("39")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.HUNGARY:
                return new IdCard.For(Country.HUNGARY)
                        .setCountryCode("36")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ICELAND:
                return new IdCard.For(Country.ICELAND)
                        .setCountryCode("354")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.IRELAND:
                return new IdCard.For(Country.IRELAND)
                        .setCountryCode("353")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ITALY:
                return new IdCard.For(Country.ITALY)
                        .setCountryCode("39")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.KOSOVO:
                return new IdCard.For(Country.KOSOVO)
                        .setCountryCode("383")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.LATVIA:
                return new IdCard.For(Country.LATVIA)
                        .setCountryCode("371")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.LIECHTENSTEIN:
                return new IdCard.For(Country.LATVIA)
                        .setCountryCode("423")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.LITHUANIA:
                return new IdCard.For(Country.LITHUANIA)
                        .setCountryCode("370")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.LUXEMBOURG:
                return new IdCard.For(Country.LUXEMBOURG)
                        .setCountryCode("352")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MALTA:
                return new IdCard.For(Country.MALTA)
                        .setCountryCode("356")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MONACO:
                return new IdCard.For(Country.MONACO)
                        .setCountryCode("377")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MONTENEGRO:
                return new IdCard.For(Country.MONTENEGRO)
                        .setCountryCode("382")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.NETHERLANDS:
                return new IdCard.For(Country.NETHERLANDS)
                        .setCountryCode("31")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.NORWAY:
                return new IdCard.For(Country.NORWAY)
                        .setCountryCode("47")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.POLAND:
                return new IdCard.For(Country.POLAND)
                        .setCountryCode("48")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.PORTUGAL:
                return new IdCard.For(Country.PORTUGAL)
                        .setCountryCode("351")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.MOLDOVA:
                return new IdCard.For(Country.MOLDOVA)
                        .setCountryCode("373")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.ROMANIA:
                return new IdCard.For(Country.ROMANIA)
                        .setCountryCode("40")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SAN_MARINO:
                return new IdCard.For(Country.SAN_MARINO)
                        .setCountryCode("378")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SERBIA:
                return new IdCard.For(Country.SERBIA)
                        .setCountryCode("381")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SLOVAKIA:
                return new IdCard.For(Country.SLOVAKIA)
                        .setCountryCode("421")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SLOVENIA:
                return new IdCard.For(Country.SLOVENIA)
                        .setCountryCode("386")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SPAIN:
                return new IdCard.For(Country.SPAIN)
                        .setCountryCode("34")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SWEDEN:
                return new IdCard.For(Country.SWEDEN)
                        .setCountryCode("46")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.SWITZERLAND:
                return new IdCard.For(Country.SWITZERLAND)
                        .setCountryCode("41")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.NORTH_MACEDONIA:
                return new IdCard.For(Country.NORTH_MACEDONIA)
                        .setCountryCode("389")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.UKRAINE:
                return new IdCard.For(Country.UKRAINE)
                        .setCountryCode("380")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            case Country.UK:
                return new IdCard.For(Country.UK)
                        .setCountryCode("44")
                        .addCard(IdType.PASSPORT)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();

            default:
                return new IdCard.For(Country.STATELESS)
                        .addCard(IdType.OTHER_ID_TYPE.replace("_", " "))
                        .create();
        }
    }
}
