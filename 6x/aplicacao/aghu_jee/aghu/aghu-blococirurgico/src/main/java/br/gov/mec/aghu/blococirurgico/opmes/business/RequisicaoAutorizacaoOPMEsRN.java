package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import br.gov.mec.aghu.blococirurgico.dao.MbcItensRequisicaoOpmesDAO;
import br.gov.mec.aghu.blococirurgico.vo.ListaMateriaisRequisicaoOpmesVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcItensRequisicaoOpmesVO;
import br.gov.mec.aghu.model.MbcItensRequisicaoOpmes;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RequisicaoAutorizacaoOPMEsRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RequisicaoAutorizacaoOPMEsRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MbcItensRequisicaoOpmesDAO mbcItensRequisicaoOpmesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5115950362349305702L;
	
	// #35482 - RN03_CONS_MATS
	public List<ListaMateriaisRequisicaoOpmesVO> pesquisarListaMateriaisRequisicaoOpmes(List<MbcItensRequisicaoOpmesVO> listaMateriais) {
		
		List<ListaMateriaisRequisicaoOpmesVO> listaMateriaisVO = new ArrayList<ListaMateriaisRequisicaoOpmesVO>();
		for (MbcItensRequisicaoOpmesVO requisicao : listaMateriais) {
			ListaMateriaisRequisicaoOpmesVO vo = new ListaMateriaisRequisicaoOpmesVO();

			vo.setCodigoDescricaoMaterial(requisicao.getItemMatDescricao());
			vo.setCompativel(requisicao.isCompativel() ? "Sim" : "Não");
			vo.setLicitado(requisicao.isRequerido() ? "Sim" : "Não");
			vo.setQuantidadeSolicitada(requisicao.getQuantidadeSolicitada());
			
			vo.setQuantidadeAutorizadaHospital(requisicao.getQuantidadeAutorizadaHospital() != null ? requisicao.getQuantidadeAutorizadaHospital().intValue() : null);
			vo.setValorUnitario(requisicao.getValorUnitarioIph());
			vo.setValorTotalSolicitado(requisicao.getQuantidadeSolicitada().intValue() * requisicao.getValorUnitarioIph().doubleValue());
			vo.setValorTabelaSus(BigDecimal.valueOf(requisicao.getQuantidadeAutorizadaHospital().intValue() * requisicao.getValorUnitarioIph().doubleValue()));
			vo.setDiferencaValor(vo.getValorTotalSolicitado() - vo.getValorTabelaSus().doubleValue() > 0 ? vo.getValorTotalSolicitado() - vo.getValorTabelaSus().doubleValue() : 0);
			
			MbcItensRequisicaoOpmes itemReq = mbcItensRequisicaoOpmesDAO.obterPorChavePrimaria(requisicao.getSeq());
			vo.setAnexoOrcamento(itemReq.getAnexoOrcamento());
			
			listaMateriaisVO.add(vo);

		}
		return listaMateriaisVO;
		
	}

	public Double calcularCompatibilidade(List<ListaMateriaisRequisicaoOpmesVO> listaMateriais, 	Double totalCompativel){			
	
		BigDecimal valorCalculado = BigDecimal.ZERO;
		for (ListaMateriaisRequisicaoOpmesVO requisicao : listaMateriais) {
				if(requisicao.getValorTabelaSus() != null){
					valorCalculado = valorCalculado.add(requisicao.getValorTabelaSus());
				}	
//				Integer qtdSolicitada = requisicao.getQuantidadeSolicitada();
//				BigDecimal vlrUnitario = requisicao.getValorUnitarioIph();
//				//Short qtdAutorizada = requisicao.getQuantidadeAutorizadaSus();
//				if(requisicao.isCompativel()){
//					totalCompativel = totalCompativel + (qtdSolicitada * vlrUnitario.doubleValue());
//				}
//				//else {
//					//totalCompativel = totalCompativel + (qtdAutorizada * vlrUnitario.doubleValue());
//				//}
			}
		
//		return totalCompativel;
		return valorCalculado.doubleValue();
		
	}
	
	// #35482 - RN04_ATLZ_TOTL
	public Double calcularIncompatibilidade(List<ListaMateriaisRequisicaoOpmesVO> listaMateriais, Double totalIncompativel){
			
		BigDecimal valorCalculado = BigDecimal.ZERO;
		for (ListaMateriaisRequisicaoOpmesVO requisicao : listaMateriais) {
				if(requisicao.getDiferencaValor() != null){
					valorCalculado = valorCalculado.add(new BigDecimal(requisicao.getDiferencaValor()));
				}	
//				Integer qtdSolicitada = requisicao.getQuantidadeSolicitada();
//				BigDecimal vlrUnitario = requisicao.getValorUnitarioIph();
//				//Short qtdAutorizada = requisicao.getQuantidadeAutorizadaSus();
//				if(requisicao.isCompativel()){
//					totalCompativel = totalCompativel + (qtdSolicitada * vlrUnitario.doubleValue());
//				}
//				//else {
//					//totalCompativel = totalCompativel + (qtdAutorizada * vlrUnitario.doubleValue());
//				//}
			}
		
//		return totalCompativel;
		return valorCalculado.doubleValue();
	}

	
	public String montarDescricaoIncompatibilidade(List<MbcItensRequisicaoOpmes> listaMateriais) {

		StringBuffer descricaoIncompatilidade = new StringBuffer();
		String quebraLinha = "\n";
			for (MbcItensRequisicaoOpmes itemRequisicao : listaMateriais) {
				if(StringUtils.isNotBlank(itemRequisicao.getDescricaoIncompativel())) {
					descricaoIncompatilidade.append(itemRequisicao.getDescricaoIncompativel().concat(quebraLinha));
				}
			}
		return descricaoIncompatilidade.toString();
	}
	
	public void atualizaIncompativel(StringBuffer incompatibilidadeEncontradas, List<MbcItensRequisicaoOpmesVO> listaPesquisada) {
		String alfabeto = "abcdefghijlmnopqrstuvxz";
		Integer index = 0;
		String quebraLinha = "\n";
		
		
		for (MbcItensRequisicaoOpmesVO item : listaPesquisada) {
			if (!item.isCompativel()) {
				if (item.getDescricaoIncompativel() != null) {
					index = escreverDescricaoIncompativel(incompatibilidadeEncontradas, alfabeto, index, quebraLinha, item);
				}
			}
		}
	}
	

	private Integer escreverDescricaoIncompativel(
			StringBuffer incompatibilidadeEncontradas, String alfabeto,
			Integer index, String quebraLinha, MbcItensRequisicaoOpmesVO item) {
		if (index == alfabeto.length() - 1) {
			index = 0;
		}
		
		incompatibilidadeEncontradas.append(Character.toString(alfabeto.charAt(index))).append(") ");
		
		incompatibilidadeEncontradas.append(item.getDescricaoIncompativel()).append(quebraLinha);
		index++;
		return index;
	}
}
