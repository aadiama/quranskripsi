package com.example.adiama.quranskripsi;

public class QuranMaqtha {
    private Long id;
    private Long surahId;
    private Long ayahNo;
    private String maqthaArabic;
    private Long tikrar;

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

    public String getMaqthaArabic() {
        return maqthaArabic;
    }
    public void setMaqthaArabic(String maqthaArabic) {
        this.maqthaArabic = maqthaArabic;
    }

    public Long getTikrar() {
        return tikrar;
    }
    public void setTikrar(Long tikrar) {
        this.tikrar = tikrar;
    }
}
