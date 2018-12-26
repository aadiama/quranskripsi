package com.example.adiama.quranskripsi;

public class Ayah {
    private Long id;
    private Long surahId;
    private Long ayahNo;
    private String ayahArabic;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurahId() {
        return surahId;
    }
    public void setSurahId(Long surahId) {
        this.surahId = surahId;
    }

    public Long getAyahNo() {
        return ayahNo;
    }
    public void setAyahNo(Long ayahNo) {
        this.ayahNo = ayahNo;
    }

    public String getAyahArabic() {
        return ayahArabic;
    }
    public void setAyahArabic(String ayahArabic) {
        this.ayahArabic = ayahArabic;
    }

}
