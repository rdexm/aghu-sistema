package br.gov.mec.aghu.business;

import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.HibernateException;
import org.hibernate.PropertyValueException;
import org.hibernate.exception.SQLGrammarException;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * 
 * @author fgka
 * @deprecated Por favor use {@link AbstractAghuCrudPersist} e encapsule dentro da ON.
 *
 */
@Deprecated
@SuppressWarnings("PMD.PackagePrivateSeamContextsManager")
public abstract class AbstractAGHUCrudOn<E extends BaseEntity> extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1581433868249417272L;

	public enum MsgExceptionStrEnum {
		
		PROPERTY("MENSAGEM_EXCECAO_CAMPO"),
		SQL("MENSAGEM_EXCECAO_SQL");
		
		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private final String str;
		
		MsgExceptionStrEnum(String str) {
			
			this.str = str;
		}
		
		@Override
		public String toString() {
			
			return this.str;
		}
	}
	
	private static List<String> razaoExecessao;	
	
	public AbstractAGHUCrudOn() {
		
		super();
	}
	
	/**
	 * Este metodo eh utilizado pelo metodo {@link #prepararAtualizacao(Object)} para buscar a entidade sem alteracao do banco.
	 * @param entidade
	 * @return
	 * @see #prepararAtualizacao(Object)
	 */
	public abstract Object getChavePrimariaEntidade(E entidade);

	/**
	 * Instancia do DAO
	 * @return
	 */
	public abstract BaseDao<E> getEntidadeDAO();
	
	/**
	 * Instancia da Objeto RN
	 * @return
	 */
	public abstract AbstractAGHUCrudRn<E> getRegraNegocio();
	
	/**
	 * Encapsulamento, com validacao de argumentos, para chamada do metodo equivalente no DAO.
	 * @param chavePrimaria
	 * @return
	 * @throws IllegalArgumentException
	 */
	public E obterPorChavePrimaria(Object chavePrimaria) {
		
		E result = null;
		
		if (chavePrimaria == null) {
			throw new IllegalArgumentException();
		}
		result = this.getEntidadeDAO().obterPorChavePrimaria(chavePrimaria);
		
		return result;
	}
	
	/**
	 * 
	 * @param entidade
	 * @return
	 * @see #obterPorChavePrimaria(Object)
	 * @see #getChavePrimariaEntidade(Object)
	 */
	public E obterPorChavePrimariaViaEntidade(E entidade) {
		
		return this.obterPorChavePrimaria(this.getChavePrimariaEntidade(entidade));
	}

	public List<String> getRazaoExecessao() {
		
		if (AbstractAGHUCrudOn.razaoExecessao == null) {
			AbstractAGHUCrudOn.razaoExecessao = new LinkedList<String>();
		}
		
		return AbstractAGHUCrudOn.razaoExecessao;
	}

	private static String getMessageFromBundle(Object... params) {
		String value = "MENSAGEM_EXCECAO_CAMPO";
		return MessageFormat.format(value, params);
	}
	
	
	private static String cleanPropertyName(String name) {
		
		String result = null;
		char[] chars = null;
		char ch = 0;

		result = "";
		chars = name.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			ch = chars[i];
			if (Character.isUpperCase(ch)) {
				result = result.concat(" ");
			}
			result = result.concat(String.valueOf(ch));			
		}
		result = result.toUpperCase();
		
		return result;
	}

	private static String cleanEntityName(String name) {
		
		String result = null;
		int ndx = 0;
		
		ndx = name.lastIndexOf('.');
		if (ndx >= 0) {
			result = name.substring(ndx + 1, name.length());
		} else {
			result = name;
		}
		
		return result;
	}
	
	private boolean logEntityRelatedThrowable(Throwable e, E entidade) {
		
		boolean result = false;
		
		if ((e != null) && (entidade != null)) {
			logError("Problemas ao persistir entidade do tipo: " + entidade.getClass().getName() + " --> obj = " + entidade.toString() + " mensagem: " + e.getLocalizedMessage());
			result = true;
		}
		
		return result;
	}

	/**
	 * Usado para documentar excecoes de estado invalido no Hibernate. <br/>
	 * Excecoes capturadas nas chamadas de metodos na camada RN
	 * Gera LOG.
	 * @param e
	 * @param entidade
	 */
	public void registrarInvalidStateException(ConstraintViolationException e, E entidade) {
		
		Set<ConstraintViolation<?>> values = null;
		
		String razao = null;
		
		if (this.logEntityRelatedThrowable(e, entidade)) {
			
			values = e.getConstraintViolations();
			for (ConstraintViolation<?> val : values) {
				logError("Valores com problemas " + val.getPropertyPath() + " = " + val.getInvalidValue() + " --> msg = " + val.getMessage());
				razao = AbstractAGHUCrudOn.getMessageFromBundle(
						MsgExceptionStrEnum.PROPERTY.toString(),
						val.getPropertyPath(),
						entidade.getClass().getSimpleName());
				this.getRazaoExecessao().add(razao);
			}
		}
	}

	/**
	 * Usado para tratar todas as excecoes lancadas pelo {@link PrePersist} e {@link PreUpdate} no momento de persistir alguma entidade no banco.
	 * @param e
	 * @param entidade
	 * @throws ApplicationBusinessException
	 */
	public void registrarExcecaoModelo(BaseRuntimeException e, E entidade) throws ApplicationBusinessException {
		
		if (this.logEntityRelatedThrowable(e, entidade)) {
			throw new ApplicationBusinessException(e.getCode());
		}		
	}
	
	/**
	 * Usado para documentar excecoes de persistencia no Hibernate. <br/>
	 * Excecoes capturadas nas chamadas de metodos na camada RN
	 * @param e
	 * @param entidade
	 */
	public void registrarPersistenceException(PersistenceException e, E entidade) {
		
		ConstraintViolationException violExcep = null;
		PropertyValueException propExcep = null;
		Throwable currExcep = null;
		
		String razao = null;
		
		if (this.logEntityRelatedThrowable(e, entidade)) {
			
			currExcep = e.getCause();
			while (currExcep != null) {
				if (currExcep instanceof ConstraintViolationException) {
					violExcep = (ConstraintViolationException) currExcep;
					violExcep.getLocalizedMessage();
					logError("Erro constraint: " + violExcep.getLocalizedMessage());
					razao = AbstractAGHUCrudOn.getMessageFromBundle(
							MsgExceptionStrEnum.SQL.toString(),
							violExcep.getLocalizedMessage());
					this.getRazaoExecessao().add(razao);
									
				} else if (currExcep instanceof PropertyValueException) {
					propExcep = (PropertyValueException) currExcep;
					logError("Erro campo: " + propExcep.getEntityName() + "." + propExcep.getPropertyName() + " : " + propExcep.getMessage());
					razao = AbstractAGHUCrudOn.getMessageFromBundle(
							MsgExceptionStrEnum.PROPERTY.toString(),
							AbstractAGHUCrudOn.cleanPropertyName(propExcep.getPropertyName()),
							AbstractAGHUCrudOn.cleanEntityName(propExcep.getEntityName()));
					this.getRazaoExecessao().add(razao);
				} else {
					logError("Erro: " + currExcep.getMessage()); 
				}
				currExcep = currExcep.getCause();
			}
		}
	}
	
	/**
	 * Usado para documentar excecoes do Hibernate. <br/>
	 * Excecoes capturadas nas chamadas de metodos na camada RN
	 * @param e
	 * @param entidade
	 */
	public void registrarHibernateException(HibernateException e, E entidade) {
		
		Throwable currExcep = null;
		
		String razao = null;
		SQLGrammarException sqlExcep = null;

		if (this.logEntityRelatedThrowable(e, entidade)) {
			
			currExcep = e;
			while (currExcep != null) {
				logError("ERRO: " + currExcep.getLocalizedMessage());
				razao = AbstractAGHUCrudOn.getMessageFromBundle(
						MsgExceptionStrEnum.SQL.toString(),
						currExcep.getLocalizedMessage());
				if (currExcep instanceof SQLGrammarException) {
					sqlExcep = (SQLGrammarException) currExcep;
					logError("ERRO SQL: estado: " + sqlExcep.getSQLState() + " consulta: "+ sqlExcep.getSQL());
				}
				this.getRazaoExecessao().add(razao);
				currExcep = currExcep.getCause();
			}
		}		
	}
	
	public void executarPrecondicao(E original, E modificada, DominioOperacoesJournal oper, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		boolean result = false;
		
		switch (oper) {
		case INS:
			result = this.getRegraNegocio().preInsercao(modificada, nomeMicrocomputador, dataFimVinculoServidor);			
			break;
		case UPD:
			result = this.getRegraNegocio().preAtualizacao(original, modificada, nomeMicrocomputador, dataFimVinculoServidor);			
			break;
		case DEL:
			result = this.getRegraNegocio().preRemocao(modificada, nomeMicrocomputador, dataFimVinculoServidor);			
			break;
		default:
			throw new IllegalArgumentException("Operacao invalida: " + oper);
		}
		if (!result) {
			throw new IllegalStateException();
		}
	}
		
	public void executarPoscondicao(E original, E modificada, DominioOperacoesJournal oper, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		boolean result = false;
		
		switch (oper) {
		case INS:
			result = this.getRegraNegocio().posInsercao(modificada, nomeMicrocomputador, dataFimVinculoServidor);			
			break;
		case UPD:
			result = this.getRegraNegocio().posAtualizacao(original, modificada, nomeMicrocomputador, dataFimVinculoServidor);			
			break;
		case DEL:
			result = this.getRegraNegocio().posRemocao(modificada, nomeMicrocomputador, dataFimVinculoServidor);
			break;
		default:
			throw new IllegalArgumentException("Operacao invalida: " + oper);
		}
		if (!result) {
			throw new IllegalStateException();
		}
	}
	
	public void executarPersistencia(E entidade, DominioOperacoesJournal oper) {
		
		switch (oper) {
		case INS:
			this.getEntidadeDAO().persistir(entidade);
			this.getEntidadeDAO().flush();
			break;
		case UPD:
			this.getEntidadeDAO().atualizar(entidade);
			this.getEntidadeDAO().flush();
			break;
		case DEL:
			this.getEntidadeDAO().remover(entidade);
			this.getEntidadeDAO().flush();
			break;
		default:
			throw new IllegalArgumentException("Operacao invalida: " + oper);
		}
	}
	
	/**
	 * <p>
	 * Este metodo deve receber a entidade a ser modificada e devolver a entidade original, desconentando ambas do Hibernate<br/>
	 * Exemplo de implementacao:<br/>
	 * </p>
	 * <p>
	 * <code>
	 * E result = null;<br/>
	 * <br/>
	 * if (entidade == null) {<br/>
	 * throw new IllegalArgumentException();<br/>
	 * }<br/>
	 * // desconecta o entidade a ser atualizada<br/>
	 * this.getEntidadeDAO().desatachar(entidade);<br/>
	 * // busca a entidade original correspondente<br/>
	 * result = this.obterPorChavePrimaria(entidade.getSeq());<br/>
	 * // desconecta a entidade original<br/>
	 * this.getEntidadeDAO().desatachar(result);<br/>
	 * <br/>
	 * return result;<br/>
	 * </code>
	 * </p> 
	 * @param entidade
	 * @return
	 * @throws BaseException
	 */
	public E prepararAtualizacao(E entidade) throws BaseException {
				
		E result = null;
	
		if (entidade == null) {
			throw new IllegalArgumentException();
		}
		// desconecta o entidade a ser atualizada
		this.getEntidadeDAO().desatachar(entidade);
		// busca a entidade original correspondente
		result = this.obterPorChavePrimariaViaEntidade(entidade);
		// desconecta a entidade original
		this.getEntidadeDAO().desatachar(result);
		
		return result;
	}

	public void processarOperacao(E entidade, DominioOperacoesJournal oper, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		E original = null;

		this.getRazaoExecessao().clear();
		if (entidade == null) {
			throw new IllegalArgumentException();
		}
		try {
			// para atualizacao, recupera a entidade original
			if (DominioOperacoesJournal.UPD.equals(oper)) {
				original = this.prepararAtualizacao(entidade);				
			}
			// pre persistencia
			this.executarPrecondicao(original, entidade, oper, nomeMicrocomputador, dataFimVinculoServidor);
			// persistencia
			this.executarPersistencia(entidade, oper);
			// pos persistencia
			this.executarPoscondicao(original, entidade, oper, nomeMicrocomputador, dataFimVinculoServidor);
		} catch (ConstraintViolationException e1) {
			this.registrarInvalidStateException(e1, entidade);
			throw new IllegalStateException(e1);
		} catch (PersistenceException e2) {
			this.registrarPersistenceException(e2, entidade);
			throw new IllegalStateException(e2);
		} catch (HibernateException e3) {
			this.logError(e3.getLocalizedMessage(), e3);
			this.registrarHibernateException(e3, entidade);
			throw new IllegalStateException(e3);
		} catch (BaseRuntimeException e4) {
			this.logError(e4.getLocalizedMessage(), e4);
			this.registrarExcecaoModelo(e4, entidade);
		}
	}

	/**
	 * Intermedia a logica de insercao de entidades.<br/>
	 * As excecoes lancadas devem ser tratadas pela Controller.
	 * @param entidade
	 * @param dataFimVinculoServidor 
	 * @throws IllegalStateException
	 * @throws BaseException
	 * @see AbstractAGHUCrudRn#preInsercao(Object)
	 * @see GenericDAO#inserir(Object)
	 * @see AbstractAGHUCrudRn#posInsercao(Object)
	 */
	public void inserir(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.processarOperacao(entidade, DominioOperacoesJournal.INS, nomeMicrocomputador, dataFimVinculoServidor);		
	}
	
	/**
	 * Intermedia a logica de atualizacao de entidades.<br/>
	 * As excecoes lancadas devem ser tratadas pela Controller.
	 * @param entidade
	 * @param dataFimVinculoServidor 
	 * @throws IllegalStateException
	 * @throws BaseException
	 * @see AbstractAGHUCrudRn#preAtualizacao(Object, Object)
	 * @see GenericDAO#atualizarDepreciado(Object)
	 * @see AbstractAGHUCrudRn#posAtualizacao(Object, Object)
	 */
	public void atualizar(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.processarOperacao(entidade, DominioOperacoesJournal.UPD, nomeMicrocomputador, dataFimVinculoServidor);		
	}

	/**
	 * Intermedia a logica de remocao de entidades.<br/>
	 * As excecoes lancadas devem ser tratadas pela Controller.
	 * @param entidade
	 * @throws IllegalStateException
	 * @throws BaseException
	 * @see AbstractAGHUCrudRn#preRemocao(Object)
	 * @see GenericDAO#remover(Object)
	 * @see AbstractAGHUCrudRn#posRemocao(Object)
	 */
	public void remover(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException, BaseException {
		
		this.processarOperacao(entidade, DominioOperacoesJournal.DEL, nomeMicrocomputador, dataFimVinculoServidor);		
	}	
}
