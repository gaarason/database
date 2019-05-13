package gaarason.database.pojo.po;


import gaarason.database.eloquent.Column;
import gaarason.database.eloquent.Primary;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SsoTenantSalt implements Serializable {

    @Primary
    @Column(nullable = false, unique = true, name = "id")
    private String id;

    @Column(nullable = false, unique = true, name = "my_tenant_code")
    private String myTenantCode;

    @Column(nullable = false, name = "third_service_code")
    private String thirdServiceCode;

    @Column(nullable = false, name = "salt")
    private String salt;

    @Column(nullable = false, name = "created_on")
    private Date createdOn;

    @Column(nullable = false, name = "created_by")
    private String createdBy;

    @Column(nullable = false, name = "modified_on")
    private Date modifiedOn;

    @Column(nullable = false, name = "modified_by")
    private String modifiedBy;

    @Column(nullable = false, name = "is_deleted")
    private Boolean deleted;
}
