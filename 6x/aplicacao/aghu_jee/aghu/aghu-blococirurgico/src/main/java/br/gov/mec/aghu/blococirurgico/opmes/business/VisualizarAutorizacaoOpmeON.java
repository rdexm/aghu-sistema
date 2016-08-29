package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.InfAutorizacoesVO;
import br.gov.mec.aghu.blococirurgico.vo.InfMateriaisNaoCompativeisVO;
import br.gov.mec.aghu.blococirurgico.vo.ListaMateriaisRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcItensRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.VisualizarAutorizacaoOpmeVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class VisualizarAutorizacaoOpmeON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(VisualizarAutorizacaoOpmeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;
	
	@Inject
	private MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO;

	@Inject
	private MbcRequisicaoOpmesDAO mbcRequisicaoOpmesDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -3733201731294589274L;
	
	protected MbcRequisicaoOpmesDAO getMbcRequisicaoOpmesDAO() {
		return mbcRequisicaoOpmesDAO;
	}
	
	protected MbcItensRequisicaoOpmesDAO getMbcItensRequisicaoOpmesDAO() {
		return mbcItensRequisicaoOpmesDAO;
	}
	
	public enum VisualizarAutorizacaoOpmeONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA;
	}

	public List<VisualizarAutorizacaoOpmeVO> carregarVizualizacaoAutorizacaoOpme(Short seqRequisicao) throws ApplicationBusinessException {

		List<VisualizarAutorizacaoOpmeVO> listaAutorizacaoVO = new ArrayList<VisualizarAutorizacaoOpmeVO>();
		VisualizarAutorizacaoOpmeVO visualVO = new VisualizarAutorizacaoOpmeVO();
		Double somaIncompativel = 0d;
		
		List<InfMateriaisNaoCompativeisVO> listaMateriaisNaoCompativeis = new ArrayList<InfMateriaisNaoCompativeisVO>();
		List<InfAutorizacoesVO> listaAutorizacoes = new ArrayList<InfAutorizacoesVO>();
		
		// #35483 - C02_CONS_REQS
		Object[] pacienteProntuario = this.getMbcRequisicaoOpmesDAO().consultarInfPacienteProntuario(seqRequisicao);
		
		if(pacienteProntuario != null){
			this.carregarInfPaciente(pacienteProntuario, visualVO);
			this.carregarInfMedico(pacienteProntuario, visualVO);
		}
		
		// #35483 - C03_CONS_MATS
		//List<Object[]> materiais = this.getMbcItensRequisicaoOpmesDAO().consultaInfMateriaisRequisicao(seqRequisicao);
		List<MbcItensRequisicaoOpmesVO> listaBind = this.blocoCirurgicoOpmesFacade.pesquisarMateriaisRequisicao(seqRequisicao, null, null);
		List<ListaMateriaisRequisicaoOpmesVO> listaMateriaisVO = this.blocoCirurgicoOpmesFacade.pesquisarListaMateriaisRequisicaoOpmes(listaBind);
		for (ListaMateriaisRequisicaoOpmesVO item : listaMateriaisVO) {
			InfMateriaisNaoCompativeisVO voMat = new InfMateriaisNaoCompativeisVO();
			
			voMat.setCodigoDescricaoMaterial(item.getCodigoDescricaoMaterial());
			voMat.setLicitado(item.getLicitado());
			voMat.setQtdeSolicitado(item.getQuantidadeSolicitada());
			voMat.setQtdeSus(item.getQuantidadeAutorizadaHospital());
			if(item.getValorUnitario() != null){
				voMat.setVlrUnitario(Double.parseDouble(item.getValorUnitario().toString()));
			}else{
				voMat.setVlrUnitario(0d);
			}
			voMat.setVlrTotalSolicitado(item.getValorTotalSolicitado());
			if(item.getValorTabelaSus() != null){
				voMat.setVlrTabelaSus(Double.parseDouble(item.getValorTabelaSus().toString()));
			}else{
				voMat.setVlrTabelaSus(0d);
			}
			voMat.setDiferencaValor(item.getDiferencaValor());
			
			somaIncompativel = somaIncompativel + item.getDiferencaValor();
			
			listaMateriaisNaoCompativeis.add(voMat);
		}
		
//		for (Object[] objMat : materiais) {
//			InfMateriaisNaoCompativeisVO voMat = new InfMateriaisNaoCompativeisVO();
//			
//			voMat.setCodigoDescricaoMaterial(objMat[0].toString());
//			voMat.setLicitado(objMat[1].toString());
//			voMat.setQtdeSolicitado(Integer.parseInt(objMat[2].toString()));
//			voMat.setQtdeSus(Integer.parseInt(objMat[3].toString()));
//			voMat.setVlrUnitario(Double.parseDouble(objMat[4].toString()));
//			voMat.setVlrTotalSolicitado(Double.parseDouble(objMat[5].toString()));
//			voMat.setVlrTabelaSus(Double.parseDouble(objMat[6].toString()));
//			voMat.setDiferencaValor(Double.parseDouble(objMat[7].toString()));
//			
//			somaIncompativel = somaIncompativel + Double.parseDouble(objMat[7].toString());
//			
//			listaMateriaisNaoCompativeis.add(voMat);
//		}
		
		// Add Total Incompatível
		visualVO.setTotalIncompativel(somaIncompativel);
		
		// #35483 - C04_CONS_AUTR
		List<Object[]> autorizacoes = this.getMbcRequisicaoOpmesDAO().consultarInfAutorizadores(seqRequisicao);
		
		for (Object[] objAut : autorizacoes) {
			InfAutorizacoesVO voAut = new InfAutorizacoesVO();
			
			voAut.setNomeServidor(objAut[0].toString());
			voAut.setCargoServidor(objAut[1].toString());
			voAut.setDataAutorizacao(DateUtil.obterDataFormatada((Date) objAut[2], "dd/MM/yyyy"));
			
			listaAutorizacoes.add(voAut);
		}
		
		
		visualVO.setInfAutorizacoes(listaAutorizacoes);
		visualVO.setInfMateriaisNaoCompativeis(listaMateriaisNaoCompativeis);
		
		listaAutorizacaoVO.add(visualVO);
		
		return listaAutorizacaoVO;
	}
	
	private void carregarInfPaciente(Object[] pacienteProntuario, VisualizarAutorizacaoOpmeVO visualVO){
		
		// Inf. Paciente
		if(pacienteProntuario[0] != null){
			visualVO.setNomePaciente(pacienteProntuario[0].toString());
		}
		if(pacienteProntuario[1] != null){
			visualVO.setConvenio(pacienteProntuario[1].toString());
		}
		if(pacienteProntuario[2] != null){
			visualVO.setProcedimento(pacienteProntuario[2].toString());
		}
		if(pacienteProntuario[3] != null){
			visualVO.setPlanoSaude(pacienteProntuario[3].toString());
		}
		if(pacienteProntuario[4] != null){
			visualVO.setProntuario(Integer.parseInt(pacienteProntuario[4].toString()));
		}
		if(pacienteProntuario[5] != null){
			visualVO.setLeito(pacienteProntuario[5].toString());
		}
		if(pacienteProntuario[6] != null){
			visualVO.setDataProcedimento((Date) pacienteProntuario[6]);
		}
		if(pacienteProntuario[7] != null){
			visualVO.setCodProcedimentoSus(Integer.parseInt(pacienteProntuario[7].toString()));
		}
		if(pacienteProntuario[8] != null){
			visualVO.setDescProcedimentoSus(pacienteProntuario[8].toString());
		}
	}
	
	private void carregarInfMedico(Object[] pacienteProntuario, VisualizarAutorizacaoOpmeVO visualVO){
		
		// Inf. Médico
		if(pacienteProntuario[9] != null){
			visualVO.setMedicoRequerente(pacienteProntuario[9].toString());
		}
		if(pacienteProntuario[10] != null){
			visualVO.setDataRequisicao((Date) pacienteProntuario[10]);
		}
		if(pacienteProntuario[11] != null){
			visualVO.setJustificativa(pacienteProntuario[11].toString());
		}
	}

	
}
