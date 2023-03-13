package ai.traceable.LdapInjectionDemo;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.*;
import java.util.Hashtable;

public class LdapTest {

    public static void main(String[] args) {
        String ldapUrl = "ldap://localhost:3890";
        String bindDN = "cn=admin,dc=example,dc=org";
        String bindPassword = "admin";
        String searchBase = "dc=example,dc=org";
        String searchFilter = "(objectClass=person)";

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
            attributes.put(new BasicAttribute("cn", args[1]));
            attributes.put(new BasicAttribute("sn", args[1]));


            // Create the InitialLdapContext object
            LdapContext ctx = new InitialLdapContext(env, null);

            String dn = "cn="+args[1]+","+searchBase;

            // Add the new entry to the LDAP context
            ctx.createSubcontext(dn, attributes);
            System.out.println("New entry added successfully!");
            String filter = args[0];


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
    }

}
