package onex7.akhirbulanku.Model;

public class TransaksiModel {

    private String user;
    private String kode;
    private String nama;
    private String norek;
    private String bank;
    private String paket;
    private Long jatuhTempo;
    private String status;

    public TransaksiModel() {

    }

    public TransaksiModel(String user, String kode, String nama, String norek, String bank, String paket, Long jatuhTempo, String status) {
        this.user = user;
        this.kode = kode;
        this.nama = nama;
        this.norek = norek;
        this.bank = bank;
        this.paket = paket;
        this.jatuhTempo = jatuhTempo;
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNorek() {
        return norek;
    }

    public void setNorek(String norek) {
        this.norek = norek;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getPaket() {
        return paket;
    }

    public void setPaket(String paket) {
        this.paket = paket;
    }

    public Long getJatuhTempo() {
        return jatuhTempo;
    }

    public void setJatuhTempo(Long jatuhTempo) {
        this.jatuhTempo = jatuhTempo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}