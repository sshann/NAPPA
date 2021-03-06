package nl.vu.cs.s2group.nappa.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.room.data.Session;
import nl.vu.cs.s2group.nappa.room.data.SessionData;

@Dao
public interface SessionDao {

    @Insert
    long insertSession(Session session);

    @Insert
    public void insertSessionData(SessionData sessionData);

    @Update
    public void updateSessionData(SessionData sessionData);

    @Query("SELECT id, date from nappa_session")
    public LiveData<List<Session>> getSessionListLiveData();

    @Query("SELECT id, date from nappa_session where date=:date")
    public Session getSession(Long date);

    @Query("SELECT id_session, id_activity_source, id_activity_destination, count_source_destination FROM nappa_session_data")
    public LiveData<List<SessionData>> getSessionDataListLiveData();

    /**
     * Gets the count of the number of instances a (source --> destination) edge has been followed, for
     * for a given idSource, for all of its destinations
     *
     * @param idSource The source {@link ActivityNode} x containing a successor set Y.
     * @return Given x, for all y of Y,  a total count of all transitions x --> y  will be returned.
     */
    @Query("SELECT id_activity_destination as idActDest, activity_name as actName, SUM(count_source_destination) as countSource2Dest " +
            "FROM nappa_session_data " +
            "LEFT JOIN nappa_activity as pfa ON pfa.id = id_activity_destination " +
            "WHERE id_activity_source = :idSource " +
            "GROUP BY id_activity_destination")
    public LiveData<List<SessionAggregate>> getCountForActivitySource(Long idSource);

    @Query("SELECT id_activity_destination as idActDest, activity_name as actName, SUM(count_source_destination) as countSource2Dest " +
            "FROM (SELECT id_activity_destination , activity_name , count_source_destination, id_session  " +
            "FROM nappa_session_data " +
            "LEFT JOIN nappa_activity as pfa ON pfa.id = id_activity_destination " +
            "WHERE id_activity_source = :idSource " +
            "ORDER BY id_session DESC) as X " +
            "WHERE " +
            "   X.id_session > (" +
            "       SELECT IFNULL(MAX(id_session) - :lastN, 0) " +
            "       FROM nappa_session_data" +
            "   ) " +
            "GROUP BY id_activity_destination ")
    public LiveData<List<SessionAggregate>> getCountForActivitySource(Long idSource, int lastN);

    class SessionAggregate {
        public Long idActDest;
        public String actName;
        public Long countSource2Dest;
    }

}
