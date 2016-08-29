package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.PdtAvalPreSedacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtCidDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDadoDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescObjetivaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescTecnicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtMedicDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtNotaAdicionalDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProfDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtSolicTempDAO;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.PdtAvalPreSedacao;
import br.gov.mec.aghu.model.PdtCidDesc;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDescObjetiva;
import br.gov.mec.aghu.model.PdtDescTecnica;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtInstrDesc;
import br.gov.mec.aghu.model.PdtMedicDesc;
import br.gov.mec.aghu.model.PdtNotaAdicional;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.PdtSolicTemp;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class CancelamentoDescricaoCirurgicaPdtRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CancelamentoDescricaoCirurgicaPdtRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtSolicTempDAO pdtSolicTempDAO;

	@Inject
	private PdtProcDAO pdtProcDAO;

	@Inject
	private PdtMedicDescDAO pdtMedicDescDAO;

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtAvalPreSedacaoDAO pdtAvalPreSedacaoDAO;

	@Inject
	private PdtDescObjetivaDAO pdtDescObjetivaDAO;

	@Inject
	private PdtInstrDescDAO pdtInstrDescDAO;

	@Inject
	private PdtProfDAO pdtProfDAO;

	@Inject
	private PdtDescTecnicaDAO pdtDescTecnicaDAO;

	@Inject
	private PdtCidDescDAO pdtCidDescDAO;

	@Inject
	private PdtDadoDescDAO pdtDadoDescDAO;

	@Inject
	private PdtNotaAdicionalDAO pdtNotaAdicionalDAO;


	@EJB
	private PdtNotaAdicionalRN pdtNotaAdicionalRN;

	@EJB
	private PdtProcRN pdtProcRN;

	@EJB
	private PdtDadoDescRN pdtDadoDescRN;

	@EJB
	private PdtDescTecnicaRN pdtDescTecnicaRN;

	@EJB
	private PdtDescricaoRN pdtDescricaoRN;

	@EJB
	private PdtMedicDescRN pdtMedicDescRN;

	@EJB
	private PdtAvalPreSedacaoRN pdtAvalPreSedacaoRN;

	@EJB
	private PdtDescObjetivaRN pdtDescObjetivaRN;

	@EJB
	private PdtCidDescRN pdtCidDescRN;

	@EJB
	private PdtProfRN pdtProfRN;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private PdtInstrDescRN pdtInstrDescRN;

	@EJB
	private PdtSolicTempRN pdtSolicTempRN;

	private static final long serialVersionUID = 2156329779615822021L;

	/**
	 * Procedure
	 * 
	 * ORADB: PDTP_DEL_TABELAS
	 */

	public void desfazCarregamentoDescricaoCirurgicaPDT(final Integer crgSeq,
			final Integer ddtSeq)
			throws ApplicationBusinessException, ApplicationBusinessException {

		final List<PdtNotaAdicional> notasAdicionais = getPdtNotaAdicionalDAO().pesquisarNotaAdicionalPorDdtSeq(ddtSeq);
		
		for (PdtNotaAdicional notaAdicional : notasAdicionais) {
			getPdtNotaAdicionalRN().excluirPdtNotaAdicional(notaAdicional);
		}

		final List<PdtCidDesc> pdtCidDescs = getPdtCidDescDAO().pesquisarPdtCidDescPorDdtSeq(ddtSeq);
		
		for (PdtCidDesc pdtCidDesc : pdtCidDescs) {
			getPdtCidDescRN().excluir(pdtCidDesc);
		}

		final List<PdtDescObjetiva> pdtDescObjetivas = getPdtDescObjetivaDAO().pesquisarDescricaoObjetivaPorDdtSeq(ddtSeq);
		
		for (PdtDescObjetiva pdtDescObjetiva : pdtDescObjetivas) {
			getPdtDescObjetivaRN().excluirPdtDescObjetiva(pdtDescObjetiva);
		}

		final List<PdtDadoDesc> pdtDadoDescs = getPdtDadoDescDAO().pesquisarDadoDescPorDdtSeq(ddtSeq);
		
		for (PdtDadoDesc pdtDadoDesc : pdtDadoDescs) {
			getPdtDadoDescRN().removerDadoDesc(pdtDadoDesc);
		}

		final List<PdtProc> pdtProcs = getPdtProcDAO().pesquisarPdtProcPorDdtSeq(ddtSeq);
		
		for (PdtProc pdtProc : pdtProcs) {
			getPdtProcRN().removerProc(pdtProc);
		}

		finalizaDesfazCarregamentoDescricaoCirurgicaPDT(crgSeq, ddtSeq);

	}

	private void finalizaDesfazCarregamentoDescricaoCirurgicaPDT(
			final Integer crgSeq, final Integer ddtSeq)
			throws ApplicationBusinessException, ApplicationBusinessException {

		final List<PdtProf> pdtProfs = getPdtProfDAO().pesquisarProfPorDdtSeq(ddtSeq);
		
		for (PdtProf pdtProf : pdtProfs) {
			getPdtProfRN().removerProf(pdtProf);
		}

		final List<PdtSolicTemp> pdtSolicTemps = getPdtSolicTempDAO().pesquisarPdtSolicTempPorDdtSeq(ddtSeq);
		
		for (PdtSolicTemp pdtSolicTemp : pdtSolicTemps) {
			getPdtSolicTempRN().excluir(pdtSolicTemp);
		}

		final List<PdtDescTecnica> pdtDescTecnicas = getPdtDescTecnicaDAO().pesquisarPdtDescTecnicaPorDdtSeq(ddtSeq);
		
		for (PdtDescTecnica pdtDescTecnica : pdtDescTecnicas) {
			getPdtDescTecnicaRN().excluir(pdtDescTecnica);
		}

		final List<PdtMedicDesc> pdtMedicDescs = getPdtMedicDescDAO().pesquisarMedicDescPorDdtSeq(ddtSeq);
		
		for (PdtMedicDesc pdtMedicDesc : pdtMedicDescs) {
			getPdtMedicDescRN().excluir(pdtMedicDesc);
		}

		// DELETE PDT_IMGS WHERE FPT_DDT_SEQ = V_DDT_SEQ; TODO eschweigert
		// 07/06/2013: fora de escopo
		// DELETE PDT_DADOS_IMGS WHERE DDT_SEQ = V_DDT_SEQ; TODO eschweigert
		// 07/06/2013: fora de escopo

		final List<PdtInstrDesc> pdtInstrDescs = getPdtInstrDescDAO()
				.pesquisarPdtInstrDescPorDdtSeq(ddtSeq);
		for (PdtInstrDesc pdtInstrDesc : pdtInstrDescs) {
			getPdtInstrDescRN().excluir(pdtInstrDesc);
		}

		// DELETE PDT_AVAL_PRE_SEDACAO WHERE SEQ = V_DDT_SEQ;
		final PdtAvalPreSedacao avalPreSedacao = getPdtAvalPreSedacaoDAO().obterPorChavePrimaria(ddtSeq);
		if(avalPreSedacao!=null){
			getPdtAvalPreSedacaoRN().removerPdtAvalPreSedacao(avalPreSedacao);
		}
		
		
		// DELETE PDT_DESCRICOES WHERE SEQ = V_DDT_SEQ;
		final PdtDescricao ptdDescricao = getPdtDescricaoDAO().obterPorChavePrimaria(ddtSeq);
		
		getPdtDescricaoRN().removerDescricao(ptdDescricao);

		
		
		

		// Certificação Digital
		getBlocoCirurgicoFacade().desbloqDocCertificacao(crgSeq, DominioTipoDocumento.PDT);
	}

	private PdtSolicTempRN getPdtSolicTempRN() {
		return pdtSolicTempRN;
	}

	protected PdtInstrDescRN getPdtInstrDescRN() {
		return pdtInstrDescRN;
	}
	
	
	
	protected PdtMedicDescRN getPdtMedicDescRN() {
		return pdtMedicDescRN;
	}

	protected PdtDescTecnicaRN getPdtDescTecnicaRN() {
		return pdtDescTecnicaRN;
	}

	protected PdtProcRN getPdtProcRN() {
		return pdtProcRN;
	}

	protected PdtDescricaoRN getPdtDescricaoRN() {
		return pdtDescricaoRN;
	}

	protected PdtAvalPreSedacaoRN getPdtAvalPreSedacaoRN() {
		return pdtAvalPreSedacaoRN;
	}
	
	
	protected PdtDadoDescRN getPdtDadoDescRN() {
		return pdtDadoDescRN;
	}

	protected PdtProfRN getPdtProfRN() {
		return pdtProfRN;
	}

	protected PdtNotaAdicionalRN getPdtNotaAdicionalRN() {
		return pdtNotaAdicionalRN;
	}

	protected PdtCidDescRN getPdtCidDescRN() {
		return pdtCidDescRN;
	}

	protected PdtDescObjetivaRN getPdtDescObjetivaRN() {
		return pdtDescObjetivaRN;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}

	protected PdtProfDAO getPdtProfDAO() {
		return pdtProfDAO;
	}

	protected PdtProcDAO getPdtProcDAO() {
		return pdtProcDAO;
	}

	protected PdtDescObjetivaDAO getPdtDescObjetivaDAO() {
		return pdtDescObjetivaDAO;
	}

	protected PdtDadoDescDAO getPdtDadoDescDAO() {
		return pdtDadoDescDAO;
	}

	protected PdtCidDescDAO getPdtCidDescDAO() {
		return pdtCidDescDAO;
	}

	protected PdtNotaAdicionalDAO getPdtNotaAdicionalDAO() {
		return pdtNotaAdicionalDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}

	protected PdtAvalPreSedacaoDAO getPdtAvalPreSedacaoDAO() {
		return pdtAvalPreSedacaoDAO;
	}
	
	protected PdtSolicTempDAO getPdtSolicTempDAO() {
		return pdtSolicTempDAO;
	}

	protected PdtDescTecnicaDAO getPdtDescTecnicaDAO() {
		return pdtDescTecnicaDAO;
	}

	protected PdtMedicDescDAO getPdtMedicDescDAO() {
		return pdtMedicDescDAO;
	}

	protected PdtInstrDescDAO getPdtInstrDescDAO() {
		return pdtInstrDescDAO;
	}

}
