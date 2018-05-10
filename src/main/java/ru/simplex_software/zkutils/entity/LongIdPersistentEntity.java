package ru.simplex_software.zkutils.entity;

import net.sf.autodao.PersistentEntity;
import org.hibernate.Hibernate;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/** This project deprected. Use autodao-utils*/
@MappedSuperclass
@Deprecated()
public class LongIdPersistentEntity implements PersistentEntity<Long> {

    public Long getPrimaryKey() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Override
    public int hashCode() {
        if(id==null){
            return 0;
        }else{
            return id.hashCode();
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if(obj instanceof LongIdPersistentEntity){
            LongIdPersistentEntity pe = (LongIdPersistentEntity) obj;
            if(id==null) {
                return false;
            }
            if (!Hibernate.getClass(this).equals(Hibernate.getClass(pe))) {
                return false;
            }

            return id.equals(pe.getId());

        }else{
            return false;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getName()+"{id="+id+"}";
    }
}
