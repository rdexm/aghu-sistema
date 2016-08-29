package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.dao.AfaPrescricaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.vo.QuantidadePrescricoesDispensacaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatorioQuantidadePrescricoesDispensacaoON extends BaseBusiness implements Serializable{

private static final Log LOG = LogFactory.getLog(RelatorioQuantidadePrescricoesDispensacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

@Inject
private AfaPrescricaoMedicamentoDAO afaPrescricaoMedicamentoDAO;

@EJB
private IParametroFacade parametroFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6616016116817124473L;

	public enum RelatorioQuantidadePrescricoesDispensacaoONExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL, MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
	}
	
	public List<QuantidadePrescricoesDispensacaoVO> pesquisarRelatorioQuantidadePrescricoesDispensacao(
			Date dataEmissaoInicio, Date dataEmissaoFim) throws ApplicationBusinessException {
		
		if(validaDataInicialMaiorQueFinal(dataEmissaoInicio, dataEmissaoFim)){
			throw new ApplicationBusinessException(RelatorioQuantidadePrescricoesDispensacaoONExceptionCode.MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL);
		}
		
		validaLimiteDiasParaRelatorio(dataEmissaoInicio, dataEmissaoFim);
		
		if(dataEmissaoFim == null) {
			dataEmissaoFim = dataEmissaoInicio;
		}
		
		List<QuantidadePrescricoesDispensacaoVO> lista = getPrescricaoMedicaFacade().pesquisarRelatorioQuantidadePrescricoesDispensacao(dataEmissaoInicio, dataEmissaoFim);
		List<QuantidadePrescricoesDispensacaoVO> listaPrescNaoEletronica = afaPrescricaoMedicamentoDAO.pesquisarRelatorioQuantidadePrescricoesDispensacao(dataEmissaoInicio, dataEmissaoFim);
		
		if((lista==null || lista.isEmpty()) && (listaPrescNaoEletronica == null || listaPrescNaoEletronica.isEmpty())) {
			throw new ApplicationBusinessException(RelatorioQuantidadePrescricoesDispensacaoONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		AfaDispensacaoMdtosDAO admDao = getAfaDispensacaoMdtosDAO();
		
		for(QuantidadePrescricoesDispensacaoVO vo : lista){
			vo.setQuantidadeItensDispensados(admDao.getQtdeItensDispensadosComPrescricaoMedicaByDataEmissao(vo.getDataEmissao()));
			vo.setDataEmissaoInicio(DateUtil.dataToString(dataEmissaoInicio, DateConstants.DATE_PATTERN_DDMMYYYY));
			vo.setDataEmissaoFim(DateUtil.dataToString(dataEmissaoFim, DateConstants.DATE_PATTERN_DDMMYYYY));
		}
		
		for(QuantidadePrescricoesDispensacaoVO vo : listaPrescNaoEletronica){
			vo.setQuantidadeItensDispensados(admDao.getQtdeItensDispensadosComPrescricaoNaoEletronicaByDataEmissao(vo.getDataEmissao()));
			vo.setDataEmissaoInicio(DateUtil.dataToString(dataEmissaoInicio, DateConstants.DATE_PATTERN_DDMMYYYY));
			vo.setDataEmissaoFim(DateUtil.dataToString(dataEmissaoFim, DateConstants.DATE_PATTERN_DDMMYYYY));
			
			Boolean existe = false;
			for (QuantidadePrescricoesDispensacaoVO l : lista) {
				if(l.getDataEmissao().equals(vo.getDataEmissao())){
					existe = true;
					l.setQuantidadePrescricoes(l.getQuantidadePrescricoes()+vo.getQuantidadePrescricoes());
					l.setQuantidadeItensDispensados(l.getQuantidadeItensDispensados() + vo.getQuantidadeItensDispensados());
				}
			}
			if(!existe){
				lista.add(vo);
			}
		}

		CoreUtil.ordenarLista(lista, QuantidadePrescricoesDispensacaoVO.Fields.DATA_EMISSAO.toString(), true);
		
		return lista;
	}
	
	private Boolean validaDataInicialMaiorQueFinal(Date dataInicial, Date dataFinal)  {

		if (dataInicial != null && dataFinal != null && dataInicial.after(dataFinal)) { 
			return true;
		}
		return false;		
	}
	
	private void validaLimiteDiasParaRelatorio(Date dataInicio,
			Date dataFim) throws ApplicationBusinessException {
		AghParametros pDifDiasMaximo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_REL_QTDE_PRESCR_DISPENS);
		Integer difDiasMaximo = pDifDiasMaximo.getVlrNumerico().intValue();
		CoreUtil.validaDifencaLimiteDiasParaRelatorio(dataInicio, dataFim, difDiasMaximo);
	}
	
	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}
	
}