package br.com.williamsbarriquero.admin.catalogo.infrastructure.video.persistence;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.*;

import br.com.williamsbarriquero.admin.catalogo.domain.castmember.CastMemberID;

@Entity(name = "VideoCastMember")
@Table(name = "videos_cast_members")
public class VideoCastMemberJpaEntity {

    @EmbeddedId
    private VideoCastMemberID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private VideoJpaEntity video;

    public VideoCastMemberJpaEntity() {
    }

    private VideoCastMemberJpaEntity(final VideoCastMemberID id, final VideoJpaEntity video) {
        this.id = id;
        this.video = video;
    }

    public static VideoCastMemberJpaEntity from(final VideoJpaEntity entity, final CastMemberID castMemberID) {
        return new VideoCastMemberJpaEntity(
                VideoCastMemberID.from(entity.getId(), UUID.fromString(castMemberID.getValue())),
                entity
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final VideoCastMemberJpaEntity that = (VideoCastMemberJpaEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getVideo(), that.getVideo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVideo());
    }

    public VideoCastMemberID getId() {
        return id;
    }

    public VideoCastMemberJpaEntity setId(VideoCastMemberID id) {
        this.id = id;
        return this;
    }

    public VideoJpaEntity getVideo() {
        return video;
    }

    public VideoCastMemberJpaEntity setVideo(VideoJpaEntity video) {
        this.video = video;
        return this;
    }
}
