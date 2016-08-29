package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoAnamnese;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmAnamnesesJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.MpmEvolucoesRN.MpmEvolucoesRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAnamnesesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAnamnesesJnDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEvolucoesDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotaAdicionalAnamnesesDAO;

@Stateless
public class MpmAnamneseRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MpmAnamneseRN.class);
	private static final long serialVersionUID = -2205745574778165789L;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private MpmAnamnesesDAO mpmAnamnesesDAO;
	
	@Inject
	private MpmEvolucoesDAO mpmEvolucoesDAO;
	
	@Inject
	private MpmAnamnesesJnDAO mpmAnamnesesJnDAO;
	
	@Inject
	private MpmNotaAdicionalAnamnesesDAO mpmNotaAdicionalAnamnesesDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	protected enum MpmAnamneseRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ANAMNESE_SEM_DESCRICAO, //
		MENSAGEM_ANAMNESE_JA_VALIDADA_OUTRO_USUARIO, //
		MENSAGEM_ERRO_ALTERAR_SITUACAO_ANAMNESE, //
		MENSAGEM_ERRO_EXCLUIR_ANAMNESE_EVOLUCAO, //
		MENSAGEM_ERRO_EXCLUIR_ANAMNESE_NOTA_ADICIONAL, //
		MENSAGEM_ERRO_CONFIGURACAO_TIPO_ITEM_ANAMNESE, //
		MENSAGEM_ERRO_EXCLUIR_ANAMNESE_OUTRO_USUARIO;
	}

	public MpmAnamneses criarMpmAnamnese(AghAtendimentos atendimento,
			RapServidores servidor) {
		MpmAnamneses novaAnamnese = new MpmAnamneses();
		Date dataAtual = new Date();
		novaAnamnese.setDthrCriacao(dataAtual);
		novaAnamnese.setPendente(DominioIndPendenteAmbulatorio.R);
		novaAnamnese.setSituacao(DominioSituacaoAnamnese.U);
		novaAnamnese.setServidor(servidor);
		novaAnamnese.setAtendimento(atendimento);
		this.getMpmAnamnesesDAO().persistir(novaAnamnese);
		return novaAnamnese;
	}

	public MpmAnamneses obterAnamnese(Integer seqAtendimento, Long seqAnamnese,
			RapServidores servidorLogado) throws ApplicationBusinessException,
			ApplicationBusinessException {
		MpmAnamneses anamnese = null;
		if (seqAnamnese != null) {
			anamnese = this.getMpmAnamnesesDAO().obterPorChavePrimaria(
					seqAnamnese);
			if (anamnese != null
					&& anamnese.getPendente() == DominioIndPendenteAmbulatorio.R) {
				String descricaoAnamneseRascunho = this.getAmbulatorioFacade()
						.getDescricaoItemAnamnese();
				anamnese.setDescricao(descricaoAnamneseRascunho);
			}
		} else if (seqAtendimento != null) {
			anamnese = this.getMpmAnamnesesDAO().obterAnamneseAtendimento(
					seqAtendimento);
		}
		return anamnese;
	}

	public void concluirAnamnese(MpmAnamneses anamnese, RapServidores servidor)
			throws ApplicationBusinessException, ApplicationBusinessException {
		validarPreenchimentoDescricaoAnamnese(anamnese, servidor);

		if (DominioIndPendenteAmbulatorio.R.equals(anamnese.getPendente())) {
			gerarJournalAnamnese(anamnese.getSeq(), servidor,
					DominioOperacoesJournal.UPD);
			salvarAnamnese(anamnese, servidor, DominioIndPendenteAmbulatorio.V);
			return;
		}

		if (DominioIndPendenteAmbulatorio.P.equals(anamnese.getPendente())) {
			gerarJournalAnamnese(anamnese.getSeq(), servidor,
					DominioOperacoesJournal.UPD);
			salvarAnamnese(anamnese, servidor, DominioIndPendenteAmbulatorio.V);
			return;
		}
		if (DominioIndPendenteAmbulatorio.V.equals(anamnese.getPendente())) {
			if (CoreUtil.modificados(servidor, anamnese.getServidor())) {
				throw new ApplicationBusinessException(
						MpmAnamneseRNExceptionCode.MENSAGEM_ANAMNESE_JA_VALIDADA_OUTRO_USUARIO);
			}
			MpmAnamneses anamneseOld = this.getMpmAnamnesesDAO().obterOriginal(
					anamnese.getSeq());
			if (CoreUtil.modificados(anamnese.getDescricao(),
					anamneseOld.getDescricao())) {
				gerarJournalAnamnese(anamnese.getSeq(), servidor,
						DominioOperacoesJournal.UPD);
				salvarAnamnese(anamnese, servidor,
						DominioIndPendenteAmbulatorio.V);
			}
		}
	}

	public void deixarPendenteAnamnese(MpmAnamneses anamnese,
			RapServidores servidor) throws ApplicationBusinessException,
			ApplicationBusinessException {
		if (anamnese.getDescricao() == null) {
			throw new ApplicationBusinessException(
					MpmAnamneseRNExceptionCode.MENSAGEM_ANAMNESE_SEM_DESCRICAO);
		}
		if (DominioIndPendenteAmbulatorio.R.equals(anamnese.getPendente())
				|| DominioIndPendenteAmbulatorio.P.equals(anamnese
						.getPendente())) {
			validarPreenchimentoDescricaoAnamnese(anamnese, servidor);
			gerarJournalAnamnese(anamnese.getSeq(), servidor,
					DominioOperacoesJournal.UPD);
			salvarAnamnese(anamnese, servidor, DominioIndPendenteAmbulatorio.P);
			return;
		}
		if (DominioIndPendenteAmbulatorio.V.equals(anamnese.getPendente())) {
			if (CoreUtil.modificados(servidor, anamnese.getServidor())) {
				throw new ApplicationBusinessException(
						MpmEvolucoesRNExceptionCode.MENSAGEM_EVOLUCAO_JA_VALIDADA_OUTRO_USUARIO);
			}
			String descricaoPadrao = getAmbulatorioFacade()
					.getDescricaoItemAnamnese();
			if (!CoreUtil.modificados(anamnese.getDescricao(), descricaoPadrao)) {
				throw new ApplicationBusinessException(
						MpmAnamneseRNExceptionCode.MENSAGEM_ANAMNESE_SEM_DESCRICAO);
			}
			boolean possuiEvolucoesDiferentesDeRascunhoOuNotasAdicionais = this
					.verificarEvolucoesNotasAdicionais(anamnese.getSeq());
			if (possuiEvolucoesDiferentesDeRascunhoOuNotasAdicionais) {
				throw new ApplicationBusinessException(
						MpmAnamneseRNExceptionCode.MENSAGEM_ERRO_ALTERAR_SITUACAO_ANAMNESE);
			}
			gerarJournalAnamnese(anamnese.getSeq(), servidor,
					DominioOperacoesJournal.UPD);
			salvarAnamnese(anamnese, servidor, DominioIndPendenteAmbulatorio.P);
		}
	}

	public void iniciarEdicaoAnamnese(MpmAnamneses anamnese,
			RapServidores servidor) {
		if (anamnese != null
				&& DominioIndPendenteAmbulatorio.P.equals(anamnese
						.getPendente())) {
			MpmAnamnesesDAO mpmAnamnesesDAO = this.getMpmAnamnesesDAO();
			// passa anamnese para em uso
			anamnese.setSituacao(DominioSituacaoAnamnese.U);
			anamnese.setServidor(servidor);
			anamnese.setDthrAlteracao(new Date());
			this.gerarJournalAnamnese(anamnese.getSeq(), servidor,
					DominioOperacoesJournal.UPD);
			// atualiza e sincroniza para atualizar a version
			mpmAnamnesesDAO.atualizar(anamnese);
			mpmAnamnesesDAO.flush();
			mpmAnamnesesDAO.refresh(anamnese);
		}
	}

	private void salvarAnamnese(MpmAnamneses anamnese, RapServidores servidor,
			DominioIndPendenteAmbulatorio pendente) throws ApplicationBusinessException {
		anamnese.setPendente(pendente);
		anamnese.setTipoItemAnamneses(obterMamTipoItemAnamnese(servidor));
		anamnese.setDthrAlteracao(new Date());
		anamnese.setServidor(servidor);
		anamnese.setSituacao(DominioSituacaoAnamnese.L);
		this.getMpmAnamnesesDAO().merge(anamnese);
	}

	private MamTipoItemAnamneses obterMamTipoItemAnamnese(RapServidores servidor)
			throws ApplicationBusinessException {
		Integer tinSeq = this.getAmbulatorioFacade()
				.getTinSeqAnamnese();
		MamTipoItemAnamneses tipoItemAnamneses = null;
		if (tinSeq != null) {
			tipoItemAnamneses = this.getAmbulatorioFacade()
					.obterMamTipoItemAnamnesesPorChavePrimaria(tinSeq);
			if (tipoItemAnamneses == null) {
				throw new ApplicationBusinessException(
						MpmAnamneseRNExceptionCode.MENSAGEM_ERRO_CONFIGURACAO_TIPO_ITEM_ANAMNESE);
			}
		}
		return tipoItemAnamneses;
	}

	private void validarPreenchimentoDescricaoAnamnese(MpmAnamneses anamnese,
			RapServidores servidor) throws ApplicationBusinessException,
			ApplicationBusinessException {
		if (anamnese.getDescricao() == null) {
			throw new ApplicationBusinessException(
					MpmAnamneseRNExceptionCode.MENSAGEM_ANAMNESE_SEM_DESCRICAO);
		}
		if (DominioIndPendenteAmbulatorio.R.equals(anamnese.getPendente())) {
			String descricaoPadrao = getAmbulatorioFacade()
					.getDescricaoItemAnamnese();
			if (anamnese.getDescricao() == null
					|| !CoreUtil.modificados(anamnese.getDescricao(),
							descricaoPadrao)) {
				throw new ApplicationBusinessException(
						MpmAnamneseRNExceptionCode.MENSAGEM_ANAMNESE_SEM_DESCRICAO);
			}
		}
	}

	private void gerarJournalAnamnese(Long anaSeq, RapServidores servidor,
			DominioOperacoesJournal operacao) {
		MpmAnamnesesJn jn = new BaseJournalFactory().getBaseJournal(operacao,
				MpmAnamnesesJn.class, servidor.getUsuario());
		MpmAnamneses anamneseOld = this.getMpmAnamnesesDAO().obterOriginal(
				anaSeq);
		jn.setSeq(anamneseOld.getSeq());
		jn.setDthrCriacao(anamneseOld.getDthrCriacao());
		jn.setDthrAlteracao(anamneseOld.getDthrAlteracao());
		jn.setPendente(anamneseOld.getPendente());
		jn.setSituacao(anamneseOld.getSituacao());
		jn.setDescricao(anamneseOld.getDescricao());
		if (anamneseOld.getTipoItemAnamneses() != null) {
			jn.setMamTipoItemAnamneses(anamneseOld.getTipoItemAnamneses()
					.getSeq());
		}

		if (anamneseOld.getServidor() != null) {
			jn.setMatriculaServidor(anamneseOld.getServidor().getId()
					.getMatricula());
			jn.setVinCodigoServidor(anamneseOld.getServidor().getId()
					.getVinCodigo());
		}
		if (anamneseOld.getAtendimento() != null) {
			jn.setAghAtendimentos(anamneseOld.getAtendimento().getSeq());
		}
		
		this.getMpmAnamnesesJnDAO().persistir(jn);
		
	}

	public boolean verificarEvolucoesNotasAdicionais(Long anaSeq) {
		boolean possuiEvolucoesDiferentesRascunho = this.getMpmEvolucoesDAO()
				.verificarEvolucoesAnamnesePorSituacao(anaSeq,
						DominioIndPendenteAmbulatorio.R, false);
		boolean possuiNotasAdicionais = this.getMpmNotaAdicionalAnamnesesDAO()
				.verificarNotasAdicionaisAnamnese(anaSeq);
		return possuiEvolucoesDiferentesRascunho || possuiNotasAdicionais;
	}
	
	public void desfazerAlteracoesAnamnese(Long seqAnamnese) {

		MpmAnamneses anamnese = this.getMpmAnamnesesDAO().obterPorChavePrimaria(seqAnamnese);
		if (anamnese.getPendente().equals(DominioIndPendenteAmbulatorio.R)) {
			this.getMpmAnamnesesDAO().remover(anamnese);
			this.getMpmAnamnesesDAO().flush();
		}
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	protected MpmAnamnesesDAO getMpmAnamnesesDAO() {
		return mpmAnamnesesDAO;
	}

	protected MpmEvolucoesDAO getMpmEvolucoesDAO() {
		return mpmEvolucoesDAO;
	}

	protected MpmAnamnesesJnDAO getMpmAnamnesesJnDAO() {
		return mpmAnamnesesJnDAO;
	}

	protected MpmNotaAdicionalAnamnesesDAO getMpmNotaAdicionalAnamnesesDAO() {
		return mpmNotaAdicionalAnamnesesDAO;
	}

}