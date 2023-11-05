package rip.warzone.anticheat.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.plugin.Plugin;
import org.reflections.util.ClasspathHelper;
import org.reflections.vfs.Vfs;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class ClassUtils {

    private ClassUtils() {
    }

    public static Collection<Class<?>> getClassesInPackage(final Plugin plugin, final String packageName) {
        final Set<Class<?>> classes=new HashSet<>();
        for ( final URL url : ClasspathHelper.forClassLoader(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader(), plugin.getClass().getClassLoader()) ) {
            final Vfs.Dir dir=Vfs.fromURL(url);
            try {
                for ( final Vfs.File file : dir.getFiles() ) {
                    final String name=file.getRelativePath().replace("/", ".").replace(".class", "");
                    if (name.startsWith(packageName)) {
                        classes.add(Class.forName(name));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                dir.close();
            }
        }
        return ImmutableSet.copyOf(classes);
    }
}
