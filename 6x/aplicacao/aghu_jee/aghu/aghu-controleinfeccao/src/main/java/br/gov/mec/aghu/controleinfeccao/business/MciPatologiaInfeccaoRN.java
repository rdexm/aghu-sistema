package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciMaterialInfecPatologiaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMicroorganismoPatologiaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMvtoMedidaPreventivasDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciPalavraChavePatologiaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciPatologiaInfeccaoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciPatologiaInfeccaoJnDAO;
import br.gov.mec.aghu.model.MciMaterialInfecPatologia;
import br.gov.mec.aghu.model.MciMicroorganismoPatologia;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.model.MciPalavraChavePatologia;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.MciPatologiaInfeccaoJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciPatologiaInfeccaoRN extends BaseBusiness {

	private static final long serialVersionUID = 4489664037701377555L;

	private static final Log LOG = LogFactory.getLog(MciPatologiaInfeccaoRN.class);

	private static final String ESPACO = " ";

	private enum MciPatologiaInfeccaoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RESTRICAO_EXCLUSAO_PATOLOGIA, MENSAGEM_DADOS_INCOMPLETOS_PATOLOGIA, MENSAGEM_PERIODO_EXCLUSAO_PATOLOGIA, MENSAGEM_VALIDACAO_USO_MASCARA, MENSAGEM_RESTRICAO_ALTERACAO_PATOLOGIA;
	}

	@Inject
	private MciPatologiaInfeccaoDAO mciPatologiaInfeccaoDAO;

	@Inject
	private MciMaterialInfecPatologiaDAO mciMaterialInfecPatologiaDAO;

	@Inject
	private MciMicroorganismoPatologiaDAO mciMicroorganismoPatologiaDAO;

	@Inject
	private MciMvtoMedidaPreventivasDAO mciMvtoMedidaPreventivasDAO;

	@Inject
	private MciPatologiaInfeccaoJnDAO mciPatologiaInfeccaoJnDAO;

	@Inject
	private MciPalavraChavePatologiaDAO mciPalavraChavePatologiaDAO;

	@EJB
	private ControleInfeccaoRN controleInfeccaoRN;

	@EJB
	private MciPalavraChavePatologiaON mciPalavraChavePatologiaON;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private BaseJournalFactory baseJournalFactory;

	public void persistirMciPatologiaInfeccao(final MciPatologiaInfeccao patologiaInfeccao) throws ApplicationBusinessException {
		this.rn8(patologiaInfeccao);

		// RN2 e RN3
		if (StringUtils.isEmpty(patologiaInfeccao.getDescricao())) {
			throw new ApplicationBusinessException(MciPatologiaInfeccaoRNExceptionCode.MENSAGEM_DADOS_INCOMPLETOS_PATOLOGIA);
		}

		final MciPatologiaInfeccao original = this.mciPatologiaInfeccaoDAO.obterOriginal(patologiaInfeccao.getSeq());

		if (original == null) { // INSERIR

			rn2(patologiaInfeccao);

			patologiaInfeccao.setCriadoEm(new Date());
			patologiaInfeccao.setServidor(this.servidorLogadoFacade.obterServidorLogado());
			
			// #41961
			patologiaInfeccao.setPermiteNotificacao(Boolean.TRUE);

			this.mciPatologiaInfeccaoDAO.persistir(patologiaInfeccao);

		} else { // ALTERAR

			rn3(original, patologiaInfeccao);

			patologiaInfeccao.setAlteradoEm(new Date());
			patologiaInfeccao.setMovimentado(this.servidorLogadoFacade.obterServidorLogado());

			this.mciPatologiaInfeccaoDAO.merge(patologiaInfeccao);

			inserirJournal(original, DominioOperacoesJournal.UPD);

		}
	}

	/**
	 * RN1
	 * 
	 * @param patologiaInfeccao
	 */
	protected void rn1(MciPatologiaInfeccao patologiaInfeccao) throws BaseException {
		rn4(patologiaInfeccao); // RN4

		StringBuilder listaMicroorganismos = new StringBuilder();

		List<MciMaterialInfecPatologia> listaMaterialInfecPatologia = this.mciMaterialInfecPatologiaDAO.pesquisarMciMicroorganismoPatologiaPorPatologia(patologiaInfeccao.getSeq());
		for (MciMaterialInfecPatologia item : listaMaterialInfecPatologia) {
			listaMicroorganismos.append(item.getId().getSeqp()).append(ESPACO);
		}

		List<MciMicroorganismoPatologia> listaMicroorganismoPatologia = this.mciMicroorganismoPatologiaDAO.pesquisarMciMicroorganismoPatologiaPorPatologia(patologiaInfeccao.getSeq());
		for (MciMicroorganismoPatologia item : listaMicroorganismoPatologia) {
			listaMicroorganismos.append(item.getId().getSeqp()).append(ESPACO);
		}

		List<MciMvtoMedidaPreventivas> listaMvtoMedidaPreventivas = this.mciMvtoMedidaPreventivasDAO.pesquisarMciMvtoMedidaPreventivasPorPatologia(patologiaInfeccao.getSeq());
		for (MciMvtoMedidaPreventivas item : listaMvtoMedidaPreventivas) {
			listaMicroorganismos.append(item.getSeq()).append(ESPACO);
		}

		if (listaMicroorganismos.length() > 0) {
			throw new ApplicationBusinessException(MciPatologiaInfeccaoRNExceptionCode.MENSAGEM_RESTRICAO_EXCLUSAO_PATOLOGIA, listaMicroorganismos.toString());
		}

		// Exclusão de registros Auxiliares: Palavras-Chave
		List<MciPalavraChavePatologia> registrosAuxiliares = this.mciPalavraChavePatologiaDAO.listarPalavraChavePatologia(patologiaInfeccao.getSeq());
		for (MciPalavraChavePatologia palavraChavePatologia : registrosAuxiliares) {
			this.mciPalavraChavePatologiaON.excluirMciPalavraChavePatologia(palavraChavePatologia);
		}

	}

	/**
	 * RN2
	 * 
	 * @param patologiaInfeccao
	 */
	protected void rn2(MciPatologiaInfeccao patologiaInfeccao) throws ApplicationBusinessException {
		if (StringUtils.isEmpty(patologiaInfeccao.getDescricao())) {
			throw new ApplicationBusinessException(MciPatologiaInfeccaoRNExceptionCode.MENSAGEM_DADOS_INCOMPLETOS_PATOLOGIA);
		}
	}

	/**
	 * RN3
	 * 
	 * @param patologiaInfeccao
	 */
	protected void rn3(final MciPatologiaInfeccao original, MciPatologiaInfeccao patologiaInfeccao) throws ApplicationBusinessException {
		if (!StringUtils.equals(original.getDescricao(), patologiaInfeccao.getDescricao())) {
			throw new ApplicationBusinessException(MciPatologiaInfeccaoRNExceptionCode.MENSAGEM_RESTRICAO_ALTERACAO_PATOLOGIA);
		}

	}

	/**
	 * RN4
	 * 
	 * @param patologiaInfeccao
	 */
	protected void rn4(MciPatologiaInfeccao patologiaInfeccao) throws BaseException {
		controleInfeccaoRN.validarNumeroDiasDecorridosCriacaoRegistro(patologiaInfeccao.getCriadoEm(), MciPatologiaInfeccaoRNExceptionCode.MENSAGEM_PERIODO_EXCLUSAO_PATOLOGIA);
	}

	/**
	 * RN8
	 * 
	 * @param patologiaInfeccao
	 * @throws ApplicationBusinessException
	 */
	protected void rn8(MciPatologiaInfeccao patologiaInfeccao) throws ApplicationBusinessException {
		if (Boolean.TRUE.equals(patologiaInfeccao.getUsoOculos()) && Boolean.FALSE.equals(patologiaInfeccao.getUsoMascara()) && Boolean.FALSE.equals(patologiaInfeccao.getUsoMascaraN95())) {
			throw new ApplicationBusinessException(MciPatologiaInfeccaoRNExceptionCode.MENSAGEM_VALIDACAO_USO_MASCARA);
		}

	}

	public void removerMciPatologiaInfeccao(final Integer seq) throws BaseException {
		MciPatologiaInfeccao patologiaInfeccao = this.mciPatologiaInfeccaoDAO.obterPorChavePrimaria(seq);
		this.rn1(patologiaInfeccao);
		this.inserirJournal(patologiaInfeccao, DominioOperacoesJournal.DEL);
		this.mciPatologiaInfeccaoDAO.remover(patologiaInfeccao);
	}

	/**
	 * Insere Journal (MciPatologiaInfeccaoJn)
	 * 
	 * @param original
	 * @param operacao
	 * @throws ApplicationBusinessException
	 */
	private void inserirJournal(final MciPatologiaInfeccao original, DominioOperacoesJournal operacao) throws ApplicationBusinessException {

		MciPatologiaInfeccaoJn journal = baseJournalFactory.getBaseJournal(operacao, MciPatologiaInfeccaoJn.class, this.servidorLogadoFacade.obterServidorLogado().getUsuario());

		journal.setSeq(original.getSeq());
		journal.setCriadoEm(original.getCriadoEm());
		journal.setDescricao(original.getDescricao());
		journal.setDuracaoMedidaPreventiva(original.getDuracaoMedidaPreventiva());
		journal.setImpNotificacao(original.getImpNotificacao());
		journal.setMovimentado(original.getMovimentado());
		journal.setNotificaSsma(original.getNotificaSsma());
		journal.setObservacao(original.getObservacao());
		journal.setObservacao2(original.getObservacao2());
		journal.setOrientacaoTratMaterial(original.getOrientacaoTratMaterial());
		journal.setOrientQuartoIndividual(original.getOrientQuartoIndividual());
		journal.setOrientUtilizacaoSanitarios(original.getOrientUtilizacaoSanitarios());
		// journal.setPermiteNotificacao(original.getPermiteNotificacao());
		journal.setPlaca(original.getPlaca());
		journal.setProfilaxiaContatos(original.getProfilaxiaContatos());
		journal.setServidor(original.getServidor());
		journal.setSituacao(original.getSituacao());
		journal.setTecnicaAsseptica(original.getTecnicaAsseptica());
		journal.setTipoMedidaPreventiva(original.getTipoMedidaPreventiva());
		journal.setTopografiaInfeccao(original.getTopografiaInfeccao());
		journal.setUsoAvental(original.getUsoAvental());
		journal.setUsoMascara(original.getUsoMascara());
		journal.setUsoQuartoPrivativo(original.getUsoQuartoPrivativo());

		this.mciPatologiaInfeccaoJnDAO.persistir(journal);
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
