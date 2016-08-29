package br.gov.mec.aghu.blococirurgico.opmes.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.vo.ConsultaPreparaOPMEFiltroVO;
import br.gov.mec.aghu.blococirurgico.vo.InfoProcdCirgRequisicaoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.MateriaisProcedimentoOPMEVO;
import br.gov.mec.aghu.dominio.DominioRequeridoItemRequisicao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConsultaPreparaOPMERN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConsultaPreparaOPMERN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	
	private enum ConsultaPreparaOPMERNExceptionCode implements BusinessExceptionCode {
		OPME_ERRO_MSG_ERRO_FILTRO, OPME_ERRO_MSG_ERRO_PROC_LIST_MAT
	}

	private static final long serialVersionUID = 6419178080587936413L;

	public List<DominioRequeridoItemRequisicao> obterDominiosRequeridoItemRequisicao(DominioSimNao licitado) {
		if(licitado != null){
			List<DominioRequeridoItemRequisicao> list = new ArrayList<DominioRequeridoItemRequisicao>();
			if (DominioSimNao.N.equals(licitado)) {
				list.add(DominioRequeridoItemRequisicao.NOV);
			} else {
				list.add(DominioRequeridoItemRequisicao.REQ);
				list.add(DominioRequeridoItemRequisicao.ADC);
			}
			return list;
		} 
		return null;
	}
	
	public void validarFiltro(ConsultaPreparaOPMEFiltroVO vo) throws ApplicationBusinessException {
		try {
			Validate.notNull(vo);
			Validate.notNull(vo.getRequisicaoSelecionada());
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(ConsultaPreparaOPMERNExceptionCode.OPME_ERRO_MSG_ERRO_FILTRO);
		}
	}

	public List<MateriaisProcedimentoOPMEVO> processarListaMateriaisDoProcedimento(List<MateriaisProcedimentoOPMEVO> list) throws ApplicationBusinessException {
		try {
			Validate.notNull(list);
			
			List<MateriaisProcedimentoOPMEVO> listRetorno = new ArrayList<MateriaisProcedimentoOPMEVO>();
			Set<Long> codigosSisTap = new HashSet<Long>();
	
			for (MateriaisProcedimentoOPMEVO vo : list) {
				if(codigosSisTap.contains(vo.getCodTabela())){
					listRetorno.add(this.montaInformacoesMaterial(vo));
				}else{
					if(DominioRequeridoItemRequisicao.REQ.equals(vo.getRequerido())){
						listRetorno.add(this.montaInformacoesSigTap(vo, false));
						listRetorno.add(this.montaInformacoesMaterial(vo));
					}else{
						formatarMaterialSUS(vo);
						formtarLicitado(vo);
						vo.setQtdeAut(vo.getQuantidadeAutorizadaHospital());
						vo.setValorUnitario(vo.getValorUnitarioIph());
						formatarValorTotal(vo);
						formatarMaterialHospital(vo);
						vo.setMaterialHosp(vo.getMaterialSUS() + " " +vo.getMaterialHosp());
						listRetorno.add(vo);
					}
					
				}
				if(vo.getCodTabela() != null){
					codigosSisTap.add(vo.getCodTabela());
				}
					
			}
			return listRetorno;
			
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(ConsultaPreparaOPMERNExceptionCode.OPME_ERRO_MSG_ERRO_PROC_LIST_MAT);
		}
	}
	
	private MateriaisProcedimentoOPMEVO montaInformacoesSigTap(MateriaisProcedimentoOPMEVO material, boolean isMaterialNovo){
		MateriaisProcedimentoOPMEVO retorno = new  MateriaisProcedimentoOPMEVO();
		formtarLicitado(material);
		formatarValorTotal(material);
		retorno.setCodTabela(material.getCodTabela());
		retorno.setLicitado(material.getLicitado());
		retorno.setQtdeAut(material.getQuantidadeAutorizadaHospital());
		retorno.setValorUnitario(material.getValorUnitarioIph());
		retorno.setValorTotal(material.getValorTotal());
		
		return retorno;
	}
	
	private MateriaisProcedimentoOPMEVO montaInformacoesMaterial(MateriaisProcedimentoOPMEVO material){
		MateriaisProcedimentoOPMEVO retorno = new  MateriaisProcedimentoOPMEVO();
		formatarMaterialHospital(material);
		retorno.setQuantidadeSolicitada(material.getQuantidadeSolicitada());
		retorno.setUnidadeMedidaCodigo(material.getUnidadeMedidaCodigo());
		retorno.setMaterialMarca(material.getMaterialMarca());
		retorno.setMaterialHosp(material.getMaterialHosp());
		return retorno;
	}

	private void formatarMaterialHospital(MateriaisProcedimentoOPMEVO vo) {
		if(DominioRequeridoItemRequisicao.NOV.equals(vo.getRequerido())){
			vo.setMaterialHosp(vo.getSolicitacaoNovoMaterial());
		} else {
			if(vo.getMaterialCodigo() != null && vo.getNome() != null){
				vo.setMaterialHosp(vo.getMaterialCodigo() + " - " + vo.getNome());
			}
		}
	}

	private void formatarValorTotal(MateriaisProcedimentoOPMEVO vo) {
		Integer qtdAutHosp = (Integer) CoreUtil.nvl(vo.getQuantidadeAutorizadaHospital(), 1);
		BigDecimal vlrUnit = (BigDecimal) CoreUtil.nvl(vo.getValorUnitarioIph(), BigDecimal.ZERO);
		vo.setValorTotal(vlrUnit.multiply(BigDecimal.valueOf(qtdAutHosp)));
	}
	
	private void formtarLicitado(MateriaisProcedimentoOPMEVO vo) {
		vo.setLicitado(DominioRequeridoItemRequisicao.NOV.equals(vo
				.getRequerido()) ? DominioSimNao.N.getDescricao() : DominioSimNao.S.getDescricao());
	}

	private void formatarMaterialSUS(MateriaisProcedimentoOPMEVO vo) {
		if(DominioRequeridoItemRequisicao.NOV.equals(vo.getRequerido())){
			vo.setMaterialSUS(getResourceBundleValue("LABEL_CONS_PREP_OPME_MATERIAL_SUS_NOV"));
		}
		
		if(DominioRequeridoItemRequisicao.ADC.equals(vo.getRequerido())){
			vo.setMaterialSUS(getResourceBundleValue("LABEL_CONS_PREP_OPME_MATERIAL_SUS_ADC"));
		}
		
		if(DominioRequeridoItemRequisicao.REQ.equals(vo.getRequerido())){
			vo.setMaterialSUS(vo.getCodTabela().toString() + " - " + vo.getDescricao());
		}
	}

	public void processarInfoProcdCirgRequisicao(InfoProcdCirgRequisicaoOPMEVO vo) throws ApplicationBusinessException {
		if (vo != null){
			formatarProntuario(vo);
		}
	}

	private void formatarProntuario(InfoProcdCirgRequisicaoOPMEVO vo) {
		if(vo.getProntuario() != null){
			vo.setStrProntuario(CoreUtil.formataProntuario(vo.getProntuario().toString()));
		}
	}
}
