package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversosDAO;
import br.gov.mec.aghu.exames.dao.AelCadCtrlQualidadesDAO;
import br.gov.mec.aghu.exames.dao.AelDadosCadaveresDAO;
import br.gov.mec.aghu.exames.dao.AelLaboratorioExternosDAO;
import br.gov.mec.aghu.exames.dao.AelProjetoPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExamesHistDAO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * Classe genérica para implementar RNs dos exames
 * 
 * ORADB package AELK_AEL_RN
 * 
 */
@Stateless
public class AelExamesRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AelExamesRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelProjetoPesquisasDAO aelProjetoPesquisasDAO;

@Inject
private AelLaboratorioExternosDAO aelLaboratorioExternosDAO;

@Inject
private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO;

@Inject
private AelCadCtrlQualidadesDAO aelCadCtrlQualidadesDAO;

@Inject
private AelDadosCadaveresDAO aelDadosCadaveresDAO;

@Inject
private AelSolicitacaoExamesHistDAO aelSolicitacaoExamesHistDAO;

	private static final long serialVersionUID = 5960016652904441045L;

	/**
	 * ORADB Function AELK_AEL_RN.RN_AELP_BUSC_CNV_ATV
	 * 
	 * @param atvSeq
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public ConvenioExamesLaudosVO rnAelpBusConvAtv(Integer atvSeq) {
		AelAtendimentoDiversos atendimentoDiverso = getAelAtendimentoDiversosDAO().obterPorChavePrimaria(atvSeq);
		if (atendimentoDiverso == null) {
			return null;
		}
		Integer pjqSeq = atendimentoDiverso.getAelProjetoPesquisas() == null ? null : atendimentoDiverso.getAelProjetoPesquisas().getSeq(); 
	    Integer laeSeq = atendimentoDiverso.getAelLaboratorioExternos() == null ? null : atendimentoDiverso.getAelLaboratorioExternos().getSeq();
	    Integer ddvSeq = atendimentoDiverso.getAelDadosCadaveres() == null ? null : atendimentoDiverso.getAelDadosCadaveres().getSeq();
	    Integer ccqSeq = atendimentoDiverso.getAelCadCtrlQualidades() == null ? null : atendimentoDiverso.getAelCadCtrlQualidades().getSeq();
	    
	    Short vCspCnvCodigo = null;
	    Byte vCspSeq = null;
	    String vDescricaoConv = null;
		
		if (pjqSeq == null && laeSeq == null && ddvSeq == null && ccqSeq == null) {
			return null;
		}
		
		if (pjqSeq != null) {
			AelProjetoPesquisas projetoPesquisas = getAelProjetoPesquisasDAO().obterProjetosPesquisaPorSeqComConvenioSaude(pjqSeq);
			if (projetoPesquisas == null) {
				return null;
			}
			else {
				vCspCnvCodigo = projetoPesquisas.getConvenioSaudePlano().getId().getCnvCodigo();
				vCspSeq = projetoPesquisas.getConvenioSaudePlano().getId().getSeq();
				vDescricaoConv = projetoPesquisas.getConvenioSaude() == null ? null : projetoPesquisas.getConvenioSaude().getDescricao();
			}
		}
		else if (laeSeq != null) {
			AelLaboratorioExternos laboratorioExternos = getAelLaboratorioExternosDAO().obterLaboratorioExternoPorSeqComConvenioSaude(laeSeq);
			if (laboratorioExternos == null) {
				return null;
			}
			else {
				vCspCnvCodigo = laboratorioExternos.getConvenio().getId().getCnvCodigo();
				vCspSeq = laboratorioExternos.getConvenio().getId().getSeq();
				vDescricaoConv = laboratorioExternos.getConvenioSaude() == null ? null : laboratorioExternos.getConvenioSaude().getDescricao();
			}
		}
		else if (ddvSeq != null) {
			AelDadosCadaveres dadosCadaveres = getAelDadosCadaveresDAO().obterDadosCadaveresPorSeqComConvenioSaude(ddvSeq);
			if (dadosCadaveres == null) {
				return null;
			}
			else {
				vCspCnvCodigo = dadosCadaveres.getConvenioSaudePlano().getId().getCnvCodigo();
				vCspSeq = dadosCadaveres.getConvenioSaudePlano().getId().getSeq();
				vDescricaoConv = dadosCadaveres.getConvenioSaude() == null ? null : dadosCadaveres.getConvenioSaude().getDescricao();
			}
		}
		else if (ccqSeq != null) {
			AelCadCtrlQualidades cadCtrlQualidades = getAelCadCtrlQualidadesDAO().obterCadCtrlQualidadesPorSeqComConvenioSaude(ccqSeq);
			if (cadCtrlQualidades == null) {
				return null;				
			}
			else {
				vCspCnvCodigo = cadCtrlQualidades.getConvenioSaudePlano().getId().getCnvCodigo();
				vCspSeq = cadCtrlQualidades.getConvenioSaudePlano().getId().getSeq();
				vDescricaoConv = cadCtrlQualidades.getConvenioSaude() == null ? null : cadCtrlQualidades.getConvenioSaude().getDescricao();
			}
		}
		
		ConvenioExamesLaudosVO retorno = new ConvenioExamesLaudosVO();
		retorno.setCodigoConvenioSaude(vCspCnvCodigo);
		retorno.setCodigoConvenioSaudePlano(vCspSeq);
		retorno.setDescricaoConvenio(vDescricaoConv);
		
		return retorno;
	}
	
	/**
	 * ORADB AELC_BUSCA_DT_EXAME_HIST
	 * 
	 * Obtém a data do exame das tabelas de histórico
	 *
	 */
	public Date obterDataExameHist(Integer soeSeq, Short seqp, String sitCodigoLiberado, String sitCodigoAreaExec) {
		AelItemSolicExameHist result = getAelSolicitacaoExamesHistDAO().obterAelItemSolicExamesHistPorSeqSeqp(soeSeq, seqp);
		//Se não estiver liberado
		if(result != null && !result.getSituacaoItemSolicitacao().getCodigo().equals(sitCodigoLiberado)){
			//A data é a de cricação da Solicitação
			return result.getSolicitacaoExame().getCriadoEm();
		}else{
			//Caso contrário, buscar a maior data de evento
			return getAelSolicitacaoExamesHistDAO().obterMaxDataHoraEvento(soeSeq, seqp, sitCodigoAreaExec);
		}
	}

	private AelSolicitacaoExamesHistDAO getAelSolicitacaoExamesHistDAO() {
		return aelSolicitacaoExamesHistDAO;
	}

	protected AelLaboratorioExternosDAO getAelLaboratorioExternosDAO() {
		return aelLaboratorioExternosDAO;
	}
	
	protected AelCadCtrlQualidadesDAO getAelCadCtrlQualidadesDAO() {
		return aelCadCtrlQualidadesDAO;
	}
	
	protected AelDadosCadaveresDAO getAelDadosCadaveresDAO() {
		return aelDadosCadaveresDAO;
	}
	
	protected AelAtendimentoDiversosDAO getAelAtendimentoDiversosDAO() {
		return aelAtendimentoDiversosDAO;
	}
	
	protected AelProjetoPesquisasDAO getAelProjetoPesquisasDAO() {
		return aelProjetoPesquisasDAO;
	}
}
