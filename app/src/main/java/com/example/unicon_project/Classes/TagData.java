package com.example.unicon_project.Classes;

public class TagData {
    private static TagData tagData = new TagData();
    public static TagData getInstance(){return tagData;}

    private static final String [][] tagList = {
            {"동인","삼덕","성내","대신","남산","대봉"},//중구
            {"신암","신천","효목",
                    "도평","불로봉무","지저",
                    "동촌","방촌","해안",
                    "안심","혁신","공산"},//동 12개
            {"내당","비산","평리","상중이동","원대"},//서
            {"이천","봉덕","대명"},//남

            {"고성","칠성","침산",
                    "산격","대현","복현",
                    "관문","태전","구암",
                    "관음","무태조야","검단",
                    "읍내","동천","노원",
                    "국우"},//북 16개
            {"범어","만촌","수성",
                    "황금","중동","상동",
                    "파동","두산","지산",
                    "범물","고산"},//수성

            {"성당","두류","본리",
                    "감삼","죽전","장기",
                    "용산","이곡","신당",
                    "월성","진천","유천",
                    "상인","도원","송현",
                    "본"},//달서
            {"화원","논공","다사",
                    "유가","옥포","현풍",
                    "가창","하빈","구지"}//달성
    };

    public static String[][] getTagList() {
        return tagList;
    }
}
