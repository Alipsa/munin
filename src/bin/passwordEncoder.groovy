/* Groovy script to generate a hash for a password used in the users table
 * This is useful if you want to / need to reset the password for a user to
 * a known one. You will need to update the password field in the users table with the
 * hashed one generated.
 *
 */
@Grab(group='org.springframework.security', module='spring-security-core', version='5.4.1')
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

def pwd = System.console().readPassword("Enter password to encode: ")
def encPwd = new BCryptPasswordEncoder().encode(String.valueOf(pwd))
println("Encoded password is: $encPwd")
