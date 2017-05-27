package mo.ipm.mengl.banphone;

/**
 * Created by laicm on 25/5/2017.
 */

public class BLEDevice {
    private final String address;
    private String name;
    private boolean status;

    private BLEDevice(String name,String address, Boolean status){
        this.name = name;
        this.address = address;
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static class Builder{
        private final String address;
        private String name;
        private boolean status = false;

        public Builder(String address){
            this.address = address;
        }



        public Builder setName(String name) {
            this.name = name;
            return this;
        }


        public Builder setStatus(boolean status) {
            this.status = status;
            return this;
        }

        public BLEDevice build(){
            return new BLEDevice(name,address,status);
        }
    }
}
