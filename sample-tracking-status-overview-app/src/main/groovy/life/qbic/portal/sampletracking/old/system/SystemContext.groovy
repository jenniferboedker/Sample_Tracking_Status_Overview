package life.qbic.portal.sampletracking.old.system

import com.liferay.portal.model.User
import com.vaadin.server.VaadinService
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.portal.utils.user.UserRelated

/**
 * <p>Utility class that provides access to some system context properties.</p>
 *
 * @since 1.0.0
 */
class SystemContext {

    /**
     * <p>Provides user information, if the application runs in an portal environment</p>
     *
     * @return the logged in {@link PortalUser}.
     * @since 1.0.0
     */
    static Optional<PortalUser> getUser() {
        String remoteUser = VaadinService.getCurrentRequest().getRemoteUser()
        if (!remoteUser) {
            return Optional.empty()
        }
        return getUserFromLiferay(remoteUser)
    }

    private static Optional<PortalUser> getUserFromLiferay(String userId) {
        User user = UserRelated.getLiferayUser(userId)
        if (!user) {
            return Optional.empty()
        }
        PortalUser portalUser = createUserFromLiferay(user)
        return Optional.of(portalUser)
    }

    private static PortalUser createUserFromLiferay(User user) {
        return new PortalUser.Builder(
                user.emailAddress,
                user.screenName,
                user.firstName,
                user.lastName,
                user.emailAddress
        ).build()
    }

}
