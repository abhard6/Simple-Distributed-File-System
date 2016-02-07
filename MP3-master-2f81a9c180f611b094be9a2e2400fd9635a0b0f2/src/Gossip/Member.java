package Gossip;
import java.io.Serializable;


public class Member implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 7791423619263531750L;
    private String address;
    private int portno;
    private int heartBeat;
    private String id;
    private long last_updated_timestamp;
    private boolean  marked_fail = false;
    private boolean marked_deleted = false;


   
   // constructor
    Member(String addr,int port,int heartbeat,String id, long last_updated_timestamp, boolean marked_fail, boolean marked_deleted)
    {
        this.address = addr;
        this.portno = port;
        this.heartBeat = heartbeat;
        this.id = id;
        this.last_updated_timestamp = last_updated_timestamp;
        this.marked_fail = marked_fail;
        this.marked_deleted = marked_deleted;
    }

    public String getAddress()
    {
        return address;
    }
    public int getport()
    {
        return portno;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Member other = (Member) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;

        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
       
        this.id = id;
    }

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartbeat) {
        this.heartBeat = heartBeat+1;
    }
   
   
    public long getTimestamp(){


        return this.last_updated_timestamp;

        }

    public void setTimestamp(){

        last_updated_timestamp = System.currentTimeMillis();

        }
   
   
    public boolean getMarkedFail(){


        return marked_fail;
        }


        public boolean getMarkedDeleted(){


        return marked_deleted;

        }


        public boolean setMarkedFail(){

        marked_fail = true;
        return marked_fail;
        }

        public boolean setMarkedDeleted(){

        marked_deleted = true;
        return marked_deleted;
        }
   
}
