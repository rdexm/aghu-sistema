package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.farmacia.business.RelatorioQuantidadePrescricoesDispensacaoON.RelatorioQuantidadePrescricoesDispensacaoONExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.farmacia.vo.MedicamentoDispensadoPorBoxVO;
import br.gov.mec.aghu.model.AfaGrupoApresMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

//Estória # 5714

@Stateless
public class RelatorioMedicamentoDispensadoPorBoxON extends BaseBusiness implements Serializable{

private static final Log LOG = LogFactory.getLog(RelatorioMedicamentoDispensadoPorBoxON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IFarmaciaFacade farmaciaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2289996868505419553L;

	public enum RelatorioMedicamentoMedicamentoDispensadoPorBoxONExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA, MENSAGEM_PERIODO_OBRIGATORIO, MENSAGEM_PACIENTE_OU_PERIODO_OBRIGATORIO
	}

	public List<MedicamentoDispensadoPorBoxVO> obterListaMedicamentosDispensadosPorBox(
			Date dataEmissaoInicio, 
			Date dataEmissaoFim,
			AghMicrocomputador aghMicrocomputador,
			AfaMedicamento medicamento,
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento,
			AfaGrupoApresMdtos afaGrupoApresMdtos, Integer pacCodigo) throws ApplicationBusinessException {

		if(validaDataInicialMaiorQueFinal(dataEmissaoInicio, dataEmissaoFim)){
			throw new ApplicationBusinessException(RelatorioQuantidadePrescricoesDispensacaoONExceptionCode.MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL);
		}

		validaLimiteDiasParaRelatorio(dataEmissaoInicio, dataEmissaoFim);

		if(dataEmissaoFim == null) {
			dataEmissaoFim = dataEmissaoInicio;
		}
		
		AghParametros parametroGrupoUsoMdto = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRPO_USO_MDTO_TB);
		List<MedicamentoDispensadoPorBoxVO> listaMedicamentomedicamentoDispensadoPorBoxVO = getAfaDispensacaoMdtosDAO().pesquisarMedicamentosDispensadosPorBox(
				dataEmissaoInicio, 
				dataEmissaoFim,
				aghMicrocomputador,
				medicamento,
				tipoApresentacaoMedicamento,
				afaGrupoApresMdtos,
				parametroGrupoUsoMdto, pacCodigo);

		if(listaMedicamentomedicamentoDispensadoPorBoxVO==null || listaMedicamentomedicamentoDispensadoPorBoxVO.isEmpty()) {
			throw new ApplicationBusinessException(RelatorioMedicamentoMedicamentoDispensadoPorBoxONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		for(MedicamentoDispensadoPorBoxVO vo: listaMedicamentomedicamentoDispensadoPorBoxVO){
			//sets padroes estão prontos pelo transformers
			processaMedicamentoDispensadoPorBox(vo, dataEmissaoInicio, dataEmissaoFim);
		}

		return listaMedicamentomedicamentoDispensadoPorBoxVO;
	}


	public void processaMedicamentoDispensadoPorBox(MedicamentoDispensadoPorBoxVO vo, Date dataEmissaoInicio, Date dataEmissaoFim ) throws ApplicationBusinessException{

		//formata seta a data de referencia inicial 
		vo.setDataEmissaoInicioEditada(DateFormatUtil.fomataDiaMesAno(dataEmissaoInicio));

		//formata e seta a data de referencia final 
		vo.setDataEmissaoFimEditada(DateFormatUtil.fomataDiaMesAno(dataEmissaoFim));
		
		//formata e seta o nome da estação dispensadora
		if(vo.getEstacaoDispensadora()!=null && !"".equals(vo.getEstacaoDispensadora())){
			vo.setEstacaoDispensadora(formataEstacao(vo.getEstacaoDispensadora()));			
		}else{
			vo.setEstacaoDispensadora("S/BOX");
		}		

		//formata a quantidade dispensada e concatena com a apresentacao
		// seta o resultado em quantidadeEditada
		String qtdeFormat = concatenaQuantidadeEApresentacao(vo.getQuantidade(), vo.getApresentacao());
		vo.setQuantidadeEditada(qtdeFormat);

		//formata e seta a concentracao no atributo concentracaoEditada
		String concentracaoFormatada = formataConcentracao(vo.getConcentracao());
		vo.setConcentracaoEditada(concentracaoFormatada);

		//formata e seta a descricao do medicamento, a apresentacao e a unidade de medidas medicas no atributo descricaoMedicamentoEditada
		String descMed = StringUtil.leftTrim(formataDescricaoMedicamento(vo.getDescricaoMedicamento(), vo.getConcentracao(), vo.getUnidadeMedidaMedica()));
		vo.setDescricaoMedicamentoEditada(descMed);

		BigDecimal valorUnit = getFarmaciaFacade().obterCustoMedioPonderado(vo.getCodMedicamento(), dataEmissaoInicio);
		vo.setCustoUnitario(valorUnit);

		BigDecimal custoTotal = calcularCustoTotal(vo.getQuantidade(), vo.getCustoUnitario());
		vo.setCustoTotal(custoTotal);

	}

	public String formataEstacao(String estacaoDispensadora) {
		return estacaoDispensadora.substring(estacaoDispensadora.lastIndexOf('\\')+1, estacaoDispensadora.length());
	}

	protected IFarmaciaFacade getFarmaciaFacade(){
		return this.farmaciaFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return (IParametroFacade) parametroFacade;
	}

	public BigDecimal calcularCustoTotal(BigDecimal quantidade, BigDecimal custoUnitario){

		BigDecimal custoTotal=null;

		if(custoUnitario == null){
			custoTotal = quantidade.multiply(BigDecimal.ZERO);			
		}	
		else {
			if(quantidade !=null && custoUnitario != null){
				custoTotal = quantidade.multiply(custoUnitario);			
			}	
			}	
		return custoTotal;		
	}

	public String formataDescricaoMedicamento(String descricaoMedicamento, BigDecimal concentracao, String unidadeMedidaMedica ){

		String descMedFormatada = null;

		if(descricaoMedicamento != null){
			descMedFormatada = descricaoMedicamento + " " + formataConcentracao(concentracao) + " " + (unidadeMedidaMedica != null ? unidadeMedidaMedica : "");
			return descMedFormatada;
		}	

		return "";
	}
	

	public String concatenaQuantidadeEApresentacao(BigDecimal quantidade, String apresentacao){
		if(quantidade!=null && apresentacao!=null){
			return quantidade.toString() + " " + apresentacao;
		}		
		return "0.0,0";
	}
	

	public String formataConcentracao(BigDecimal concentracao) {
		Locale locBR = new Locale("pt", "BR");//Brasil 
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
		dfSymbols.setDecimalSeparator(',');
		DecimalFormat format;
		if(concentracao != null)
		{
			format = new DecimalFormat("#,###,###,###,##0.####", dfSymbols);
			return format.format(concentracao);
		}

		return " ";
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


	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return afaDispensacaoMdtosDAO;
	}




}