/* Groovy script to generate a hash for a password used in the users table */
@Grab(group='org.springframework.security', module='spring-security-core', version='5.4.1')
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

def pwd = System.console().readPassword("Enter password to encode: ");
def encPwd = new BCryptPasswordEncoder().encode(String.valueOf(pwd))
println("Encoded password is: $encPwd")
