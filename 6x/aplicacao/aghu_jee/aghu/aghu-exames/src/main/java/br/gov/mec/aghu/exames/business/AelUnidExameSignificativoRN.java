package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelUnidExameSignificativoDAO;
import br.gov.mec.aghu.model.AelUnidExameSignificativo;
import br.gov.mec.aghu.model.AelUnidExameSignificativoId;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * RN da entidade {@link AelUnidExameSignificativo}
 * 
 * @author luismoura
 * 
 */
@Stateless
public class AelUnidExameSignificativoRN extends BaseBusiness {
	private static final long serialVersionUID = 8190241636980279100L;

	@Inject
	private AelUnidExameSignificativoDAO aelUnidExameSignificativoDAO;

	private static final Log LOG = LogFactory.getLog(AelUnidExameSignificativoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Persistir dados de exames significativos
	 * 
	 * Web Service #36157
	 * 
	 * @param unfSeq
	 * @param exaSigla
	 * @param matAnlsSeq
	 * @param data
	 * @param matricula
	 * @param vinCodigo
	 * @param indPreNatal
	 */
	public void persistir(Short unfSeq, String exaSigla, Integer matAnlsSeq, Date data, Integer matricula, Short vinCodigo, Boolean indPreNatal, Boolean indCargaExame) {

		AelUnidExameSignificativoId id = this.obterAelUnidExameSignificativoId(unfSeq, exaSigla, matAnlsSeq);

		AelUnidExameSignificativo aelUnidExameSignificativo = getAelUnidExameSignificativoDAO().obterPorChavePrimaria(id);

		String preNatal = (indPreNatal != null ? DominioSimNao.getInstance(indPreNatal).toString() : null);

		if (aelUnidExameSignificativo != null) {
			// altera valores
			this.ajustaValoresAelUnidExameSignificativo(aelUnidExameSignificativo, matricula, vinCodigo, preNatal, indCargaExame);
			// salva
			getAelUnidExameSignificativoDAO().atualizar(aelUnidExameSignificativo);
		} else {
			// cria o id
			aelUnidExameSignificativo = new AelUnidExameSignificativo();
			aelUnidExameSignificativo.setId(id);
			// altera valores
			this.ajustaValoresAelUnidExameSignificativo(aelUnidExameSignificativo, matricula, vinCodigo, preNatal, indCargaExame);
			aelUnidExameSignificativo.setCriadoEm(data);			
			// salva
			getAelUnidExameSignificativoDAO().persistir(aelUnidExameSignificativo);
		}
	}

	/**
	 * Excluir dados de exames significativos
	 * 
	 * Web Service #36158
	 * 
	 * @param unfSeq
	 * @param exaSigla
	 * @param matAnlsSeq
	 */
	public void remover(Short unfSeq, String exaSigla, Integer matAnlsSeq) {
		AelUnidExameSignificativoId id = this.obterAelUnidExameSignificativoId(unfSeq, exaSigla, matAnlsSeq);
		getAelUnidExameSignificativoDAO().removerPorId(id);
	}

	/**
	 * Cria o id de AelUnidExameSignificativo com os parâmetros
	 * 
	 * @param unfSeq
	 * @param exaSigla
	 * @param matAnlsSeq
	 * @return
	 */
	private AelUnidExameSignificativoId obterAelUnidExameSignificativoId(Short unfSeq, String exaSigla, Integer matAnlsSeq) {
		AelUnidExameSignificativoId id = new AelUnidExameSignificativoId();
		id.setUnfSeq(unfSeq);
		id.setEmaExaSigla(exaSigla);
		id.setEmaManSeq(matAnlsSeq);
		return id;
	}

	/**
	 * Ajusta os valores de AelUnidExameSignificativo
	 * 
	 * @param aelUnidExameSignificativo
	 * @param matricula
	 * @param vinCodigo
	 * @param preNatal
	 * @param indCargaExame
	 */
	private void ajustaValoresAelUnidExameSignificativo(AelUnidExameSignificativo aelUnidExameSignificativo, Integer matricula, Short vinCodigo,
			String preNatal, Boolean indCargaExame) {
		aelUnidExameSignificativo.setSerMatricula(matricula);
		aelUnidExameSignificativo.setSerVinCodigo(vinCodigo);
		aelUnidExameSignificativo.setIndPrenatal(preNatal);
		aelUnidExameSignificativo.setIndCargaExame(indCargaExame);
	}

	protected AelUnidExameSignificativoDAO getAelUnidExameSignificativoDAO() {
		return this.aelUnidExameSignificativoDAO;
	}
}