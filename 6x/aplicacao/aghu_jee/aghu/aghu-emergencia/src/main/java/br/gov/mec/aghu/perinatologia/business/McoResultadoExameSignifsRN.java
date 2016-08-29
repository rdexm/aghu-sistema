package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoResultadoExameSignifs;
import br.gov.mec.aghu.model.McoResultadoExameSignifsId;
import br.gov.mec.aghu.model.McoResultadoExameSignifsJn;
import br.gov.mec.aghu.perinatologia.dao.McoResultadoExameSignifsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoResultadoExameSignifsJnDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * 
 * @author luismoura
 * 
 *         Regras de negócio da entidade {@link McoResultadoExameSignifs}
 * 
 */
@Stateless
public class McoResultadoExameSignifsRN extends BaseBusiness {
	private static final long serialVersionUID = 2050569185197445450L;

	@Inject
	private McoResultadoExameSignifsDAO mcoResultadoExameSignifsDAO;

	@Inject
	private McoResultadoExameSignifsJnDAO mcoResultadoExameSignifsJnDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	/**
	 * Insere um registro na journal de McoResultadoExameSignifs
	 * 
	 * @param resultadoExameOrigi
	 * @param operacao
	 */
	private void inserirJournal(McoResultadoExameSignifs resultadoExameOrigi, DominioOperacoesJournal operacao) {
		McoResultadoExameSignifsJn resultadoExameJn = BaseJournalFactory.getBaseJournal(operacao, McoResultadoExameSignifsJn.class, usuario.getLogin());
		resultadoExameJn.setCriadoEm(resultadoExameOrigi.getCriadoEm());
		resultadoExameJn.setDataRealizacao(resultadoExameOrigi.getDataRealizacao());
		resultadoExameJn.setEexSeq(resultadoExameOrigi.getExameExterno() != null ? resultadoExameOrigi.getExameExterno().getSeq() : null);
		resultadoExameJn.setEmaExaSigla(resultadoExameOrigi.getEmaExaSigla());
		resultadoExameJn.setEmaManSeq(resultadoExameOrigi.getEmaManSeq());
		resultadoExameJn.setGsoPacCodigo(resultadoExameOrigi.getId().getGsoPacCodigo());
		resultadoExameJn.setGsoSeqp(resultadoExameOrigi.getId().getGsoSeqp());
		resultadoExameJn.setIseSeqp(resultadoExameOrigi.getIseSeqp());
		resultadoExameJn.setIseSoeSeq(resultadoExameOrigi.getIseSoeSeq());
		resultadoExameJn.setResultado(resultadoExameOrigi.getResultado());
		resultadoExameJn.setSeqp(resultadoExameOrigi.getId().getSeqp());
		resultadoExameJn.setSerMatricula(resultadoExameOrigi.getSerMatricula());
		resultadoExameJn.setSerVinCodigo(resultadoExameOrigi.getSerVinCodigo());
		mcoResultadoExameSignifsJnDAO.persistir(resultadoExameJn);
	}


	/**
	 * RN09 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * @ORADB MCO_RESULTADO_EXAME_SIGNIFS.MCOT_RXS_BRI – ­trigger before insert
	 * 
	 * @param mcoResultadoExameSignifs
	 */
	private void preIncluir(McoResultadoExameSignifs mcoResultadoExameSignifs) {
		// 1. Setar a data/hora atual no campo CRIADO_EM
		mcoResultadoExameSignifs.setCriadoEm(new Date());
		// 2. Setar no campo SER_MATRICULA a matrícula do usuário logado no sistema
		mcoResultadoExameSignifs.setSerMatricula(usuario.getMatricula());
		// 3. Setar no campo SER_VIN_CODIGO o código do vínculo do usuário logado no sistema
		mcoResultadoExameSignifs.setSerVinCodigo(usuario.getVinculo());
	}

	/**
	 * RN07 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * Inserção de registro em MCO_RESULTADO_EXAME_SIGNIFS
	 * 
	 * @param mcoResultadoExameSignifs
	 */
	public void incluir(McoResultadoExameSignifs mcoResultadoExameSignifs) {
		// 2. Executa RN09 – trigger before insert
		this.preIncluir(mcoResultadoExameSignifs);
		// 3. Persiste o registro na base
		mcoResultadoExameSignifsDAO.persistir(mcoResultadoExameSignifs);
	}

	/**
	 * RN10 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * @ORADB MCO_RESULTADO_EXAME_SIGNIFS.MCOT_RXS_ARU – ­trigger after update
	 * 
	 * @param mcoResultadoExameSignifs
	 */
	private void posAtualizar(McoResultadoExameSignifs mcoResultadoExameSignifs, McoResultadoExameSignifs resultadoExameOrigi) {
		// 1. Se algum campo de MCO_RESULTADO_EXAME_SIGNIFS tiver sido modificado inserir o registro original na journal
		if (CoreUtil.modificados(mcoResultadoExameSignifs.getDataRealizacao(), resultadoExameOrigi.getDataRealizacao())
				|| CoreUtil.modificados(mcoResultadoExameSignifs.getExameExterno(), resultadoExameOrigi.getExameExterno())
				|| CoreUtil.modificados(mcoResultadoExameSignifs.getEmaExaSigla(), resultadoExameOrigi.getEmaExaSigla())
				|| CoreUtil.modificados(mcoResultadoExameSignifs.getEmaManSeq(), resultadoExameOrigi.getEmaManSeq())
				|| CoreUtil.modificados(mcoResultadoExameSignifs.getIseSeqp(), resultadoExameOrigi.getIseSeqp())
				|| CoreUtil.modificados(mcoResultadoExameSignifs.getIseSoeSeq(), resultadoExameOrigi.getIseSoeSeq())
				|| CoreUtil.modificados(mcoResultadoExameSignifs.getResultado(), resultadoExameOrigi.getResultado())
				|| CoreUtil.modificados(mcoResultadoExameSignifs.getSerMatricula(), resultadoExameOrigi.getSerMatricula())
				|| CoreUtil.modificados(mcoResultadoExameSignifs.getSerVinCodigo(), resultadoExameOrigi.getSerVinCodigo())) {
			// MCO_RESULTADO_EXAME_SIGNIFS_JN. Atribuir ao campo JN_OPERATION o valor ‘UPD’.
			this.inserirJournal(resultadoExameOrigi, DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * RN08 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * Atualização de registro em MCO_RESULTADO_EXAME_SIGNIFS
	 * 
	 * @param mcoResultadoExameSignifs
	 */
	public void atualizarDescricaoDataRealizacao(McoResultadoExameSignifsId mcoResultadoExameSignifsId, String resultado, Date dataRealizacao) {
		McoResultadoExameSignifs resultadoExame = this.mcoResultadoExameSignifsDAO.obterPorChavePrimaria(mcoResultadoExameSignifsId);
		if(resultadoExame != null){
			McoResultadoExameSignifs resultadoExameOrig = this.mcoResultadoExameSignifsDAO.obterOriginal(resultadoExame);
			if (CoreUtil.modificados(resultadoExame.getResultado(), resultado)
					|| CoreUtil.modificados(resultadoExame.getDataRealizacao(), dataRealizacao)) {
				resultadoExame.setResultado(resultado);
				resultadoExame.setDataRealizacao(dataRealizacao);
				this.mcoResultadoExameSignifsDAO.atualizar(resultadoExame);
				this.posAtualizar(resultadoExame, resultadoExameOrig);
			}
		}
	}

	/**
	 * RN08 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * Atualização de registro em MCO_RESULTADO_EXAME_SIGNIFS
	 * 
	 * @param mcoResultadoExameSignifs
	 */
	public void atualizar(McoResultadoExameSignifs mcoResultadoExameSignifs) {
		McoResultadoExameSignifs resultadoExameOrig = this.mcoResultadoExameSignifsDAO.obterOriginal(mcoResultadoExameSignifs);
		this.mcoResultadoExameSignifsDAO.atualizar(mcoResultadoExameSignifs);
		this.posAtualizar(mcoResultadoExameSignifs, resultadoExameOrig);
	}

	/**
	 * RN011 de #25644 - INFORMAR CARGA EXAME
	 * 
	 * Exclusão de registros em MCO_RESULTADO_EXAME_SIGNIFS
	 * 
	 * @param mcoResultadoExameSignifs
	 */
	public void excluir(McoResultadoExameSignifsId mcoResultadoExameSignifsId) {
		McoResultadoExameSignifs resultadoExame = this.mcoResultadoExameSignifsDAO.obterPorChavePrimaria(mcoResultadoExameSignifsId);
		if(resultadoExame != null){
			// 2. Se o registro corrente não possuir SEQP descartar o registro
			this.mcoResultadoExameSignifsDAO.remover(resultadoExame);
			// 3. Se o registro corrente possuir SEQP excluí-lo da base de dados e executar RN12 (trigger after delete)
			this.inserirJournal(resultadoExame, DominioOperacoesJournal.DEL);
		}
	}

}