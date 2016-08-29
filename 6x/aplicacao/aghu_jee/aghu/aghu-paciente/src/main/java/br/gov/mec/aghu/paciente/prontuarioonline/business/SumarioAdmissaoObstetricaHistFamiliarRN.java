package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.model.AipHistoriaFamiliares;
import br.gov.mec.aghu.model.McoGestacaoPacientes;
import br.gov.mec.aghu.paciente.dao.AipHistoriaFamiliaresDAO;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class SumarioAdmissaoObstetricaHistFamiliarRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaHistFamiliarRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPerinatologiaFacade perinatologiaFacade;

@Inject
private AipHistoriaFamiliaresDAO aipHistoriaFamiliaresDAO;

	private static final long serialVersionUID = -4365999015762922691L;

	/**
	 * 
	 * @return
	 */
	private AipHistoriaFamiliaresDAO getAipHistoriaFamiliaresDAO() {
		return aipHistoriaFamiliaresDAO;
	}
	
	/**
	 * Q_GPA + Q_INA
	 * @param vo
	 */
	public void executarQGpa(SumarioAdmissaoObstetricaInternacaoVO vo) {
		Integer pacCodigo = (Integer)vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO);
		List<McoGestacaoPacientes> lista = getPerinatologiaFacade().listarGestacoesPacientePorCodigoPaciente(pacCodigo);
		
		if(lista != null) {
			
			List<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO> gestacoesAnteriores = new ArrayList<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO>();
			
			for(McoGestacaoPacientes gestacao : lista) {
				
				if(gestacao != null) {
					SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO gestacaoAnterior = new SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO();
					//GPA_COMPLICACOES
					gestacaoAnterior.setComplicacoes(gestacao.getComplicacoes());
					//GPA_ANO
					gestacaoAnterior.setAno(gestacao.getAno());
					//QINA
					if(gestacao.getMcoIndicacaoNascimento() != null){
						gestacaoAnterior.setInaDescricao(gestacao.getMcoIndicacaoNascimento().getDescricao());
					}
					//GPA_RN_PESO 
					if (gestacao.getRnPeso() != null){
						StringBuilder sb = new StringBuilder();
						sb.append(gestacao.getRnPeso()).append(" g");
						gestacaoAnterior.setPeso(sb.toString());
					}
					//GPA_CLASSIFICACAO
					if(gestacao.getClassificacao() != null) {
						gestacaoAnterior.setClassificacao(gestacao.getClassificacao().getDescricao());
					}
					//GPA_RN_SITUACAO
					if(gestacao.getRnClassificacao() != null) {
						gestacaoAnterior.setSituacao(gestacao.getRnClassificacao().getDescricao());
					}
					gestacoesAnteriores.add(gestacaoAnterior);
				}
			}
			vo.setGestacoesAnteriores(gestacoesAnteriores);
		}
	}
	
	/**
	 * Q_HIF
	 * @param vo
	 */
	public void executarQHif(SumarioAdmissaoObstetricaInternacaoVO vo) {
		Integer pacCodigo = (Integer)vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO);
		AipHistoriaFamiliares historicoFamiliar = getAipHistoriaFamiliaresDAO().obterPorChavePrimaria(pacCodigo);
		
		if(historicoFamiliar != null) {				
			// mae
			if(DominioSimNao.S.equals(historicoFamiliar.getIndMaePeE())) {
				String antecedenteMae = getResourceBundleValue("MSG_MAE_GESTANTE_JA_TEVE_PRE_ECLAMPSIA_OU_ECLAMPSIA");
				if(StringUtils.isNotBlank(antecedenteMae)) {
					antecedenteMae = antecedenteMae.concat(", ");
				}
				vo.setAntecedenteMae(antecedenteMae);
			}
			// irma
			if(DominioSimNao.S.equals(historicoFamiliar.getIndIrmaPeE())) {
				vo.setAntecedenteIrma(getResourceBundleValue("MSG_IRMA_GESTANTE_JA_TEVE_PRE_ECLAMPSIA_OU_ECLAMPSIA"));
			}
			// diabetes
			if(DominioSimNao.S.equals(historicoFamiliar.getIndDiabeteNaFamilia())) {
				vo.setDiabeteFamilia(getResourceBundleValue("MSG_DIABETE_NA_FAMILIA").concat(","));
			}
			// doencas congenitas
			if(DominioSimNao.S.equals(historicoFamiliar.getIndDoencasCongenitas())) {
				vo.setDoencasCongenitas(getResourceBundleValue("MSG_DOENCAS_CONGENITAS_NA_FAMILIA"));
			}
			// observacao
			if(StringUtils.isNotBlank(historicoFamiliar.getObservacao())) {
				vo.setHifObservacao(historicoFamiliar.getObservacao());
			}
		}
	}
	
	/**
	 * Acesso ao modulo perinatologia
	 * @return
	 */
	private IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
}
