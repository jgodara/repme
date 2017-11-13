package jgodara.repme.key;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.mapping.Table;
import org.hibernate.type.Type;

public class HibernateKeyGenerator implements PersistentIdentifierGenerator, Configurable {

	private Class returnClass;
	private String tableName;
	private String selectMaxQuery;
	
	private String entityName;
	private String keyGenerationMode;
	
	
	public String[] sqlCreateStrings(Dialect arg0) throws HibernateException {

		return new String[]{};
	}

	public String[] sqlDropStrings(Dialect arg0) throws HibernateException {

		return new String[]{};
	}

	public Object generatorKey() {
		return tableName;
	}

	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		
		
		if(keyGenerationMode.equals("ON"))
		{
			
		}
		else if(keyGenerationMode.equals("OFF"))
		{
			final Serializable id = session.getEntityPersister( entityName, object ) 
									.getIdentifier(entityName, session);
			if (id != null)
				return id;
		}
		else   // AUTO
		{
			final Serializable id = session.getEntityPersister( entityName, object )
									.getIdentifier(object, session);

			
			if (id!=null)
			{
				String sid = id.toString();
				if (!sid.equals("") && Long.parseLong(sid)>0) 
					return id;
			}
		}
			
		long result = 0;
		try
		{
			result = KeyGenerator.nextval(tableName);

		} catch (Exception e) {
			throw new HibernateException(e);
		}
		return createNumber(result, returnClass);
	}
	
	private Number createNumber(long value, Class clazz) throws IdentifierGenerationException {
		if ( clazz==Long.class ) {
			return new Long(value);
		}
		else if ( clazz==Integer.class ) {
			return new Integer( (int) value );
		}
		else if ( clazz==Short.class ) {
			return new Short( (short) value );
		}
		else {
			throw new IdentifierGenerationException("this id generator generates long, integer, short");
		}
	}

	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
		
		entityName = params.getProperty(ENTITY_NAME);
		if (entityName==null) {
			throw new MappingException("no entity name");
		}
		
		returnClass = type.getReturnedClass();
		
		String table = params.getProperty("tables");
	
		if (table==null) table = params.getProperty(PersistentIdentifierGenerator.TABLES);
		tableName = table;
		
		String column = params.getProperty("column");
		String batchSize = params.getProperty("batchSize", "0");
		String highLevelRatio = params.getProperty("highLevelRatio", "80");
		keyGenerationMode = params.getProperty("switch", "AUTO").toUpperCase();
		if (column==null) column = params.getProperty(PersistentIdentifierGenerator.PK);
		String schema = params.getProperty(PersistentIdentifierGenerator.SCHEMA);
        String catalog = params.getProperty(PersistentIdentifierGenerator.CATALOG);
    
		selectMaxQuery = "select max(" + column + ") from " + Table.qualify( catalog, schema, table );
		
		KeyGenerator.addKeyMap(table.toUpperCase(), Integer.parseInt(batchSize), Integer.parseInt(highLevelRatio), selectMaxQuery);
		
	}

}
