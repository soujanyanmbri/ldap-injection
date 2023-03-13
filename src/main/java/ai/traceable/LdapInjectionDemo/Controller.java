package ai.traceable.LdapInjectionDemo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

@RestController
public class Controller {
    @RequestMapping ("/addUser")
    String addUser(@RequestParam(value = "id", defaultValue = "0") String searchParam) {


        String ldapUrl = "ldap://ec2-13-233-252-117.ap-south-1.compute.amazonaws.com:3890";
        String bindDN = "cn=admin,dc=example,dc=org";
        String bindPassword = "admin";
        String searchBase = "dc=example,dc=org";

        try {
            // Set up the environment properties for the InitialLdapContext
            Hashtable<String, Object> env = new Hashtable<String, Object>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, bindDN);
            env.put(Context.SECURITY_CREDENTIALS, bindPassword);

            // Add dummy data
            Attributes attributes = new BasicAttributes();
            Attribute objectClass = new BasicAttribute("objectclass");
            objectClass.add("top");
            objectClass.add("person");
            attributes.put(objectClass);
            attributes.put(new BasicAttribute("cn", searchParam));
            attributes.put(new BasicAttribute("sn", searchParam));


            // Create the InitialLdapContext object
            LdapContext ctx = new InitialLdapContext(env, null);

            String dn = "cn="+searchParam+","+searchBase;
            ctx.createSubcontext(dn, attributes);
            ctx.close();
        } catch (NamingException e) {
            System.err.println("NamingException: " + e.getMessage());
        }
        return "User Added!";
    }
    @RequestMapping("/searchUser")
    String hello(@RequestParam(value = "id", defaultValue = "0") String searchParam) {

        String ldapUrl = "ldap://ec2-13-233-252-117.ap-south-1.compute.amazonaws.com:3890";
        String bindDN = "cn=admin,dc=example,dc=org";
        String bindPassword = "admin";
        String searchBase = "dc=example,dc=org";

        try {
            // Set up the environment properties for the InitialLdapContext
            Hashtable<String, Object> env = new Hashtable<String, Object>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUrl);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, bindDN);
            env.put(Context.SECURITY_CREDENTIALS, bindPassword);


            // Create the InitialLdapContext object
            LdapContext ctx = new InitialLdapContext(env, null);
            String filter = "(cn=" + searchParam + ")";;


            // Perform a search
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> results = ctx.search(searchBase, filter, searchControls);

            // Print out the search results
            while (results.hasMore()) {
                SearchResult result = results.next();
                System.out.println(result.getNameInNamespace());
            }

            // Close the context
            ctx.close();

        } catch (NamingException e) {
            System.err.println("NamingException: " + e.getMessage());
        }
        return "Done!";
    }
}