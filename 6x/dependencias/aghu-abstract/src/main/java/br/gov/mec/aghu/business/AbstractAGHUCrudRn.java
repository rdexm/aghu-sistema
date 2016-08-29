package br.gov.mec.aghu.business;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.BaseEntity;

@SuppressWarnings({"PMD.AghuTooManyMethods","PMD.PackagePrivateSeamContextsManager"})
public abstract class AbstractAGHUCrudRn<E extends BaseEntity> extends BaseBusiness implements Serializable {
	
	private static final long serialVersionUID = -3240858992805760416L;

	/**
	 * Retorna a data corrente
	 * 
	 * @return
	 */
	public Date getDataCriacao() {

		return Calendar.getInstance().getTime();
	}

	/**
	 * Limpa o argumento, retornando <code>null</code> caso ele contenha somente
	 * espacos.
	 * 
	 * @param valor
	 * @param somenteMaiusculas
	 * @return
	 * @see String#trim()
	 * @see String#toUpperCase()
	 */
	public static String adequarTextoObrigatorio(final String valor, final boolean somenteMaiusculas) {

		String result = null;

		if (valor != null) {
			result = valor.trim();
			if (somenteMaiusculas){
				result = result.isEmpty() ? null : result.toUpperCase();
			}
		}

		return result;
	}

	/**
	 * @param valor
	 * @return
	 * @see #adequarTextoObrigatorio(String, boolean)
	 */
	public static String adequarTextoObrigatorio(final String valor) {

		return AbstractAGHUCrudRn.adequarTextoObrigatorio(valor, true);
	}

	/**
	 * Before Statement Insert: <b>BSI</b>
	 * 
	 * @param entidade
	 * @return
	 * @throws BaseException
	 * @see {@link #preInsercao(Object)}
	 */

	public boolean bsiPreInsercaoStatement(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return true;
	}

	/**
	 * Before Row Delete: <b>BRD</b>
	 * 
	 * @param entidade
	 * @return
	 * @throws BaseException
	 * @see {@link #preInsercao(Object)}
	 */
	public boolean briPreInsercaoRow(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		return true;
	}

	/**
	 * <p>
	 * Encapsula a chamada dos seguintes metodos na ordem especificada:<br/>
	 * {@link #bsiPreInsercaoStatement(Object)} -- before statement insert <br/>
	 * {@link #briPreInsercaoRow(Object)} -- before row insert<br/>
	 * </p>
	 * 
	 * @param entidade
	 * @return se insercao eh permitida
	 * @see #bsiPreInsercaoStatement(Object)
	 * @see #briPreInsercaoRow(Object)
	 */
	public boolean preInsercao(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		boolean result = false;

		if (entidade == null) {
			throw new IllegalArgumentException();
		}
		result = this.bsiPreInsercaoStatement(entidade, nomeMicrocomputador, dataFimVinculoServidor) && this.briPreInsercaoRow(entidade, nomeMicrocomputador, dataFimVinculoServidor);

		return result;
	}

	/**
	 * After Row Insert: <b>ARI</b>
	 * 
	 * @param entidade
	 * @return
	 * @throws BaseException
	 * @see {@link #posInsercao(Object)}
	 */
	public boolean ariPosInsercaoRow(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		return true;
	}

	/**
	 * After Statement Insert: <b>ASI</b>
	 * 
	 * @param entidade
	 * @return
	 * @throws BaseException
	 * @see {@link #posInsercao(Object)}
	 */
	public boolean asiPosInsercaoStatement(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return true;
	}

	/**
	 * <p>
	 * Encapsula a chamada dos seguintes metodos na ordem especificada:<br/>
	 * {@link #ariPosInsercaoRow(Object)} -- after row insert<br/>
	 * {@link #asiPosInsercaoStatement(Object)} -- after statement insert <br/>
	 * </p>
	 * 
	 * @param entidade
	 * @param dataFimVinculoServidor 
	 * @return se insercao foi bem sucedida
	 * @see #ariPosInsercaoRow(Object)
	 * @see #asiPosInsercaoStatement(Object)
	 */
	public boolean posInsercao(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		boolean result = false;

		if (entidade == null) {
			throw new IllegalArgumentException();
		}
		result = this.ariPosInsercaoRow(entidade, nomeMicrocomputador, dataFimVinculoServidor) && this.asiPosInsercaoStatement(entidade, nomeMicrocomputador, dataFimVinculoServidor);

		return result;
	}

	/**
	 * Before Statement Update: <b>BSU</b>
	 * 
	 * @param original
	 * @param modificada
	 * @return
	 * @throws BaseException
	 * @see {@link #preAtualizacao(Object, Object)}
	 */
	public boolean bsuPreAtualizacaoStatement(E original, E modificada)
			throws BaseException {
		return true;
	}

	/**
	 * Before Row Update: <b>BRU</b>
	 * 
	 * @param original
	 * @param modificada
	 * @return
	 * @throws BaseException
	 * @see {@link #preAtualizacao(Object, Object)}
	 */
	public boolean bruPreAtualizacaoRow(E original, E modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return true;
	}

	/**
	 * <p>
	 * Encapsula a chamada dos seguintes metodos na ordem especificada:<br/>
	 * {@link #bsuPreAtualizacaoStatement(Object, Object)} -- before statement
	 * update <br/>
	 * {@link #bruPreAtualizacaoRow(Object, Object)} -- before row update<br/>
	 * </p>
	 * 
	 * @param original
	 * @param modificada
	 * @param dataFimVinculoServidor 
	 * @return se alteracao eh permitida
	 * @throws BaseException
	 * @see #bsuPreAtualizacaoStatement(Object, Object)
	 * @see #bruPreAtualizacaoRow(Object, Object)
	 */
	public boolean preAtualizacao(E original, E modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		boolean result = false;

		if (original == null) {
			throw new IllegalArgumentException();
		}
		if (modificada == null) {
			throw new IllegalArgumentException();
		}
		result = this.bsuPreAtualizacaoStatement(original, modificada)
				&& this.bruPreAtualizacaoRow(original, modificada, nomeMicrocomputador, dataFimVinculoServidor);

		return result;
	}

	/**
	 * After Row Update: <b>ARU</b>
	 * 
	 * @param original
	 * @param modificada
	 * @return
	 * @throws BaseException
	 * @see {@link #posInsercao(Object)}
	 */
	public boolean aruPosAtualizacaoRow(E original, E modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return true;
	}

	/**
	 * After Statement Update: <b>ASU</b>
	 * 
	 * @param original
	 * @param modificada
	 * @return
	 * @throws BaseException
	 * @see {@link #posAtualizacao(Object, Object)}
	 */
	public boolean asuPosAtualizacaoStatement(E original, E modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return true;
	}

	/**
	 * <p>
	 * Encapsula a chamada dos seguintes metodos na ordem especificada:<br/>
	 * </p>
	 * 
	 * @param original
	 * @param modificada
	 * @param dataFimVinculoServidor 
	 * @return se alteracao foi bem sucedida
	 */
	public boolean posAtualizacao(E original, E modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		boolean result = false;

		if (original == null) {
			throw new IllegalArgumentException();
		}
		if (modificada == null) {
			throw new IllegalArgumentException();
		}
		result = this.aruPosAtualizacaoRow(original, modificada, nomeMicrocomputador, dataFimVinculoServidor)
				&& this.asuPosAtualizacaoStatement(original, modificada, nomeMicrocomputador, dataFimVinculoServidor);

		return result;
	}

	/**
	 * Before Statement Delete: <b>BSD</b>
	 * 
	 * @param entidade
	 * @return
	 * @throws BaseException
	 * @see {@link #preRemocao(Object)}
	 */

	public boolean bsdPreRemocaoStatement(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return true;
	}

	/**
	 * Before Row Delete: <b>BRD</b>
	 * 
	 * @param entidade
	 * @return
	 * @throws BaseException
	 * @see {@link #preRemocao(Object)}
	 */
	public boolean brdPreRemocaoRow(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		return true;
	}

	/**
	 * <p>
	 * Encapsula a chamada dos seguintes metodos na ordem especificada:<br/>
	 * {@link #bsdPreRemocaoStatement(Object)} -- before statement delete <br/>
	 * {@link #brdPreRemocaoRow(Object)} -- before row delete<br/>
	 * </p>
	 * 
	 * @param entidade
	 * @return se remocao eh permitida
	 * @throws BaseException 
	 * @see #bsdPreRemocaoStatement(Object)
	 * @see #brdPreRemocaoRow(Object)
	 */

	public boolean preRemocao(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		boolean result = false;

		if (entidade == null) {
			throw new IllegalArgumentException();
		}
		result = this.bsdPreRemocaoStatement(entidade, nomeMicrocomputador, dataFimVinculoServidor) && this.brdPreRemocaoRow(entidade, nomeMicrocomputador, dataFimVinculoServidor);

		return result;
	}

	/**
	 * After Row Delete: <b>ARD</b>
	 * 
	 * @param entidade
	 * @return
	 * @throws BaseException
	 * @see {@link #posRemocao(Object)}
	 */
	public boolean ardPosRemocaoRow(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		return true;
	}

	/**
	 * After Statement Delete: <b>ASD</b>
	 * 
	 * @param entidade
	 * @return
	 * @throws BaseException
	 * @see {@link #posRemocao(Object)}
	 */
	public boolean asdPosRemocaoStatement(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		return true;
	}

	/**
	 * <p>
	 * Encapsula a chamada dos seguintes metodos na ordem especificada:<br/>
	 * {@link #ardPosRemocaoRow(Object)} -- after row delete<br/>
	 * {@link #asdPosRemocaoStatement(Object)} -- after statement delete <br/>
	 * </p>
	 * 
	 * @param entidade
	 * @return se remocao foi bem sucedida
	 * @see #ardPosRemocaoRow(Object)
	 * @see #asdPosRemocaoStatement(Object)
	 */
	public boolean posRemocao(E entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		boolean result = false;

		if (entidade == null) {
			throw new IllegalArgumentException();
		}
		result = this.ardPosRemocaoRow(entidade, nomeMicrocomputador, dataFimVinculoServidor) && this.asdPosRemocaoStatement(entidade, nomeMicrocomputador, dataFimVinculoServidor);

		return result;
	}
	
}
