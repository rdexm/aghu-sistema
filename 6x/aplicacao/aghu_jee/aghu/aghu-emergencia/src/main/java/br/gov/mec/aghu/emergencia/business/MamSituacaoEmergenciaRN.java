package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgDestinoDAO;
import br.gov.mec.aghu.emergencia.dao.MamExtratoTriagemDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaJnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamCaractSitEmerg;
import br.gov.mec.aghu.model.MamCaractSitEmergJn;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamSituacaoEmergenciaJn;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Regras de negócio relacionadas à entidade MamSituacaoEmergencia
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamSituacaoEmergenciaRN extends BaseBusiness {
	private static final long serialVersionUID = -4849204111108849502L;

	public enum MamSituacaoEmergenciaRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SUCESSO_CADASTRO_SITUACAO, //
		MENSAGEM_SUCESSO_EDICAO_SITUACAO, //
		MENSAGEM_SUCESSO_CADASTRO_CARACT_SITUACAO, //
		MENSAGEM_SUCESSO_EDICAO_CARACT_SITUACAO, //
		MENSAGEM_SUCESSO_EXCLUSAO_SIT_EMERGS, //
		MENSAGEM_SUCESSO_EXCLUSAO_CARACT_SIT_EMERG, //
		MENSAGEM_ERRO_EXCLUSAO_SIT_EMERGS, //
		MENSAGEM_ERRO_CARACT_SIT_JA_CADASTRADA, //
		MENSAGEM_ERRO_EXCLUSAO_DESTINOS, //
		MENSAGEM_ERRO_EXCLUSAO_EXTRATO_TRIAGENS, //
		MENSAGEM_ERRO_EXCLUSAO_TRIAGENS, //
		;
	}

	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;

	@Inject
	private MamSituacaoEmergenciaJnDAO mamSituacaoEmergenciaJnDAO;

	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;

	@Inject
	private MamCaractSitEmergJnDAO mamCaractSitEmergJnDAO;

	@Inject
	private MamEmgDestinoDAO mamEmgDestinoDAO;

	@Inject
	private MamExtratoTriagemDAO mamExtratoTriagemDAO;

	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Exclui o registro executando as regras de pre-delete
	 * 
	 * RN02 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param situacaoEmergencia
	 * @throws ApplicationBusinessException
	 */
	public void excluir(MamSituacaoEmergencia situacaoEmergencia) throws ApplicationBusinessException {

		if (!this.mamSituacaoEmergenciaDAO.contains(situacaoEmergencia)) {
			situacaoEmergencia = mamSituacaoEmergenciaDAO.obterPorChavePrimaria(situacaoEmergencia.getSeq());
		}

		Long numCaracteristicas = mamCaractSitEmergDAO.pesquisarCaracteristicaSituacaoEmergenciaCount(situacaoEmergencia.getSeq());
		if (numCaracteristicas != null && numCaracteristicas.longValue() > 0) {
			throw new ApplicationBusinessException(MamSituacaoEmergenciaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_SIT_EMERGS);
		}

		Long numEmgDestino = mamEmgDestinoDAO.pesquisarEmgDestinoSituacaoEmergenciaCount(situacaoEmergencia.getSeq());
		if (numEmgDestino != null && numEmgDestino.longValue() > 0) {
			throw new ApplicationBusinessException(MamSituacaoEmergenciaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_DESTINOS);
		}

		Long numExtratoTriagem = mamExtratoTriagemDAO.pesquisarExtratoTriagemSituacaoEmergenciaCount(situacaoEmergencia.getSeq());
		if (numExtratoTriagem != null && numExtratoTriagem.longValue() > 0) {
			throw new ApplicationBusinessException(MamSituacaoEmergenciaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_EXTRATO_TRIAGENS);
		}

		Long numTriagem = mamTriagensDAO.pesquisarTriagemSituacaoEmergenciaCount(situacaoEmergencia.getSeq());
		if (numTriagem != null && numTriagem.longValue() > 0) {
			throw new ApplicationBusinessException(MamSituacaoEmergenciaRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_TRIAGENS);
		}

		// Insere registro na tabela MAM_SITUACAO_EMERGENCIAS_JN através do insert I2
		// Setar no campo JN_OPERATION ‘DEL’
		this.inserirJournalSituacaoEmergencia(situacaoEmergencia, DominioOperacoesJournal.DEL);

		this.mamSituacaoEmergenciaDAO.remover(situacaoEmergencia);
	}

	/**
	 * Insere um registro na journal de MamSituacaoEmergencia
	 * 
	 * @param mamSituacaoEmergenciaOriginal
	 * @param operacao
	 */
	private void inserirJournalSituacaoEmergencia(MamSituacaoEmergencia mamSituacaoEmergenciaOriginal, DominioOperacoesJournal operacao) {

		MamSituacaoEmergenciaJn mamSituacaoEmergenciaJn = BaseJournalFactory.getBaseJournal(operacao, MamSituacaoEmergenciaJn.class, usuario.getLogin());

		mamSituacaoEmergenciaJn.setSeq(mamSituacaoEmergenciaOriginal.getSeq());
		mamSituacaoEmergenciaJn.setDescricao(mamSituacaoEmergenciaOriginal.getDescricao());
		mamSituacaoEmergenciaJn.setIndSituacao(mamSituacaoEmergenciaOriginal.getIndSituacao());
		mamSituacaoEmergenciaJn.setIndUsoTriagem(mamSituacaoEmergenciaOriginal.getIndUsoTriagem());
		mamSituacaoEmergenciaJn.setCriadoEm(mamSituacaoEmergenciaOriginal.getCriadoEm());
		mamSituacaoEmergenciaJn.setSerMatricula(mamSituacaoEmergenciaOriginal.getRapServidores().getId().getMatricula());
		mamSituacaoEmergenciaJn.setSerVinCodigo(mamSituacaoEmergenciaOriginal.getRapServidores().getId().getVinCodigo());

		mamSituacaoEmergenciaJnDAO.persistir(mamSituacaoEmergenciaJn);
	}

	/**
	 * Insere um registro de MamSituacaoEmergencia, executando trigger de pre-insert
	 * 
	 * RN01 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @ORADB MAM_SITUACAO_EMERGENCIAS.MAMT_SEG_BRI
	 * @param situacaoEmergencia
	 * @throws ApplicationBusinessException
	 */
	private void inserir(MamSituacaoEmergencia situacaoEmergencia) throws ApplicationBusinessException {
		situacaoEmergencia.setCriadoEm(new Date());		
		situacaoEmergencia.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		this.mamSituacaoEmergenciaDAO.persistir(situacaoEmergencia);
	}

	/**
	 * Atualiza um registro de MamSituacaoEmergencia, executando trigger de pre-update
	 * 
	 * RN01 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @ORADB MAM_SITUACAO_EMERGENCIAS.MAMT_SEG_ARU
	 * @param situacaoEmergencia
	 */
	private void alterar(MamSituacaoEmergencia situacaoEmergencia, MamSituacaoEmergencia situacaoEmergenciaOriginal) throws ApplicationBusinessException {
		
		situacaoEmergencia.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));

		if (CoreUtil.modificados(situacaoEmergencia.getDescricao(), situacaoEmergenciaOriginal.getDescricao())
				|| CoreUtil.modificados(situacaoEmergencia.getIndSituacao(), situacaoEmergenciaOriginal.getIndSituacao())
				|| CoreUtil.modificados(situacaoEmergencia.getIndUsoTriagem(), situacaoEmergenciaOriginal.getIndUsoTriagem())
				|| CoreUtil.modificados(situacaoEmergencia.getCriadoEm(), situacaoEmergenciaOriginal.getCriadoEm())
				|| CoreUtil.modificados(situacaoEmergencia.getRapServidores(), situacaoEmergenciaOriginal.getRapServidores())) {

			this.inserirJournalSituacaoEmergencia(situacaoEmergenciaOriginal, DominioOperacoesJournal.UPD);
		}

		mamSituacaoEmergenciaDAO.atualizar(situacaoEmergencia);
	}

	/**
	 * Insere ou atualiza um registro de MamSituacaoEmergencia, executando trigger de pre-insert ou pre-update
	 * 
	 * RN01 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @param situacaoEmergencia
	 */
	public void persistir(MamSituacaoEmergencia situacaoEmergencia) throws ApplicationBusinessException {
		if (situacaoEmergencia.getSeq() == null) {
			this.inserir(situacaoEmergencia);
		} else {
			MamSituacaoEmergencia situacaoEmergenciaOriginal = this.mamSituacaoEmergenciaDAO.obterOriginal(situacaoEmergencia);
			this.alterar(situacaoEmergencia, situacaoEmergenciaOriginal);
		}
	}

	/**
	 * Insere um registro de MamCaractSitEmerg, executando trigger de pre-insert
	 * 
	 * RN03 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @ORADB MAM_CARACT_SIT_EMERGS. MAMT_CSI_BRI
	 * @param caracSituacaoEmergencia
	 * @throws ApplicationBusinessException
	 */
	public void inserirMamCaractSitEmerg(MamCaractSitEmerg caracSituacaoEmergencia) throws ApplicationBusinessException {

		MamCaractSitEmerg caracSitEm = mamCaractSitEmergDAO.obterPorChavePrimaria(caracSituacaoEmergencia.getId());
		if (caracSitEm != null) {
			throw new ApplicationBusinessException(MamSituacaoEmergenciaRNExceptionCode.MENSAGEM_ERRO_CARACT_SIT_JA_CADASTRADA);
		}
		caracSituacaoEmergencia.setCriadoEm(new Date());		
		caracSituacaoEmergencia.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		this.mamCaractSitEmergDAO.persistir(caracSituacaoEmergencia);
	}

	/**
	 * Insere um registro na journal de MamCaractSitEmerg
	 * 
	 * @param mamCaractSitEmergOriginal
	 * @param operacao
	 */
	private void inserirJournalCaracteristicaSituacaoEmergencia(MamCaractSitEmerg mamCaractSitEmergOriginal, DominioOperacoesJournal operacao) {

		MamCaractSitEmergJn mamCaractSitEmergJn = BaseJournalFactory.getBaseJournal(operacao, MamCaractSitEmergJn.class, usuario.getLogin());

		mamCaractSitEmergJn.setSegSeq(mamCaractSitEmergOriginal.getId().getSegSeq());
		mamCaractSitEmergJn.setCaracteristica(mamCaractSitEmergOriginal.getId().getCaracteristica().getCodigo());

		String indSituacao = mamCaractSitEmergOriginal.getIndSituacao() != null ? mamCaractSitEmergOriginal.getIndSituacao().toString() : null;
		mamCaractSitEmergJn.setIndSituacao(indSituacao);

		mamCaractSitEmergJn.setCriadoEm(mamCaractSitEmergOriginal.getCriadoEm());
		mamCaractSitEmergJn.setSerMatricula(mamCaractSitEmergOriginal.getRapServidores().getId().getMatricula());
		mamCaractSitEmergJn.setSerVinCodigo(mamCaractSitEmergOriginal.getRapServidores().getId().getVinCodigo());
		
		mamCaractSitEmergJnDAO.persistir(mamCaractSitEmergJn);
	}

	/**
	 * Ativa/Inativa um registro de MamCaractSitEmerg, executando trigger de pre-update
	 * 
	 * RN05 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @ORADB MAM_CARACT_SIT_EMERGS. MAMT_CSI_ARU
	 * @param caracSituacaoEmergencia
	 * @throws ApplicationBusinessException
	 */
	public void ativarInativarMamCaractSitEmerg(MamCaractSitEmerg caracSituacaoEmergencia) throws ApplicationBusinessException {

		this.inserirJournalCaracteristicaSituacaoEmergencia(caracSituacaoEmergencia, DominioOperacoesJournal.UPD);

		DominioSituacao situacao = DominioSituacao.A.equals(caracSituacaoEmergencia.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A;
		caracSituacaoEmergencia.setIndSituacao(situacao);		
		caracSituacaoEmergencia.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		this.mamCaractSitEmergDAO.atualizar(caracSituacaoEmergencia);
	}

	/**
	 * Exclui um registro de MamCaractSitEmerg, executando trigger de pre-delete
	 * 
	 * RN04 de #12167 – Manter cadastro de situações da emergência
	 * 
	 * @ORADB MAM_CARACT_SIT_EMERGS. MAMT_SEG_ARD
	 * @param caracSituacaoEmergencia
	 * @throws ApplicationBusinessException
	 */
	public void excluirMamCaractSitEmerg(MamCaractSitEmerg caracSituacaoEmergencia) throws ApplicationBusinessException {

		if (!this.mamCaractSitEmergDAO.contains(caracSituacaoEmergencia)) {
			caracSituacaoEmergencia = mamCaractSitEmergDAO.obterPorChavePrimaria(caracSituacaoEmergencia.getId());
		}

		this.inserirJournalCaracteristicaSituacaoEmergencia(caracSituacaoEmergencia, DominioOperacoesJournal.DEL);

		this.mamCaractSitEmergDAO.remover(caracSituacaoEmergencia);
	}
}
