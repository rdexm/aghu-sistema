package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioAgruparRelMensal;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.RelatorioMensalMaterialVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class GerarRelatorioMensalMaterialON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(GerarRelatorioMensalMaterialON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	@Inject
	private SceEstoqueGeralDAO sceEstoqueGeralDAO;

	private static final long serialVersionUID = -2556113587693865867L;

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected SceEstoqueGeralDAO getSceEstoqueGeralDAO(){
		return sceEstoqueGeralDAO;
	}

	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO(){
		return sceMovimentoMaterialDAO;
	}

	/**
	 * Realiza a pesquisa dos dados do relatório mensal de materiais
	 * @param dataCompetencia
	 * @param siglasTipoMovimento
	 * @return
	 * @throws BaseException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<RelatorioMensalMaterialVO> pesquisarDadosRelatorioMensalMaterial(Date dataCompetencia, DominioAgruparRelMensal agrupar) throws BaseException {
		List<ScoGrupoMaterial> listGrupoMaterial = getComprasFacade().pesquisaRelMensalMateriais(dataCompetencia,null,agrupar);

		RelatorioMensalMaterialVO relatorioMensalMaterialVO = null;
		List<RelatorioMensalMaterialVO> listRelMensalMaterial = new ArrayList<RelatorioMensalMaterialVO>();

		//Parâmetros utilizados nas consultas:
		//ValorNR  --"P_TMV_DOC_NR"
		AghParametros parametroNR = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);
		//ValorDA  --"P_TMV_DOC_DA"
		AghParametros parametroDA = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_DA);
		//ValorRM  --"P_TMV_DOC_RM"
		AghParametros parametroRM = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_RM);
		//ValorDF  --"P_TMV_DOC_DF"
		AghParametros parametroDF = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_DF);
		//ValorAjste  --"P_TMV_AJUSTE_POS"
		AghParametros parametroPOS = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_AJUSTE_POS);
		//ValorAjsts  --"P_TMV_AJUSTE_NEG"
		AghParametros parametroNEG = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_AJUSTE_NEG);
		//ValorPI  --"P_TMV_DOC_PI"
		AghParametros parametroPI = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_PI);
		//valorOutrosEntrada  17 - "P_TMV_DOC_TR"     18 - "P_TMV_DOC_TR_COMPL"
		Boolean oEntrada = true;//Parâmetro para reaproveitamento da query para valor outros entrada/saída
		AghParametros parametroDocTr = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_TR);
		AghParametros parametroDocTrCompl = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_TR_COMPL);

		//fornecedor HCPA
		AghParametros parametroHcpa = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);



		//Totais dos campos do relatório
		Long totalEstocavel = 0l;
		Long totalNaoEstocavel = 0l;
		BigDecimal  totalVlrInicial = BigDecimal.ZERO;
		BigDecimal  totalVlrNr = BigDecimal.ZERO;
		BigDecimal  totalVlrDa = BigDecimal.ZERO;
		BigDecimal  totalVlrRm = BigDecimal.ZERO;
		BigDecimal  totalVlrDf = BigDecimal.ZERO;
		BigDecimal  totalVlrAjste = BigDecimal.ZERO;
		BigDecimal  totalVlrAjsts = BigDecimal.ZERO;
		BigDecimal  totalVlrPi = BigDecimal.ZERO;
		BigDecimal  totalOutrosEntrada = BigDecimal.ZERO;
		BigDecimal  totalOutrosSaida = BigDecimal.ZERO;
		BigDecimal  totalVlrAtual = BigDecimal.ZERO;
		BigDecimal  totalCobDias = BigDecimal.ZERO;
		BigDecimal cpTotValorRm = BigDecimal.ZERO;
		BigDecimal cpTotValorDa = BigDecimal.ZERO;
		String fechamento="";
		BigDecimal bDzero = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		
		DecimalFormat decimalFormat = new DecimalFormat(); 
		decimalFormat.setMinimumFractionDigits(2);  
		

		for(ScoGrupoMaterial grupoMaterial: listGrupoMaterial){

			relatorioMensalMaterialVO = new RelatorioMensalMaterialVO();

			relatorioMensalMaterialVO.setCodigo(grupoMaterial.getCodigo());
			relatorioMensalMaterialVO.setDescricao(grupoMaterial.getDescricao());


			//Estoc
			Long materialEstocavel = this.getComprasFacade().pesquisaScoMateriaisRelMensal(grupoMaterial.getCodigo(), DominioSimNao.S);
			if(materialEstocavel!=null){
				relatorioMensalMaterialVO.setEstocavel(materialEstocavel);
				totalEstocavel = totalEstocavel +materialEstocavel;
				relatorioMensalMaterialVO.setTotalEstocavel(totalEstocavel);
			}else{
				relatorioMensalMaterialVO.setEstocavel(0l);
			}
			//Não Estoc


			Long materialNaoEstoc = this.getComprasFacade().pesquisaScoMateriaisRelMensal(grupoMaterial.getCodigo(), DominioSimNao.N);


			if(materialNaoEstoc!=null){
				relatorioMensalMaterialVO.setNaoEstocavel(materialNaoEstoc);
				totalNaoEstocavel = totalNaoEstocavel + materialNaoEstoc;
				relatorioMensalMaterialVO.setTotalNaoEstocavel(totalNaoEstocavel);
			}else{
				relatorioMensalMaterialVO.setNaoEstocavel(0l);
			}

			//ValorInicial
			Calendar calendarDataCompetenciaInicial = Calendar.getInstance();
			calendarDataCompetenciaInicial.setTime(dataCompetencia);
			calendarDataCompetenciaInicial.add(Calendar.MONTH, -1);
			BigDecimal valorInicial = getSceEstoqueGeralDAO().pesquisarSomaValorEstocGeral(calendarDataCompetenciaInicial.getTime(), grupoMaterial.getCodigo(),parametroHcpa.getVlrNumerico().intValue());
			relatorioMensalMaterialVO.setVlrInicial(valorInicial);
			totalVlrInicial = totalVlrInicial.add(valorInicial);
			relatorioMensalMaterialVO.setTotalVlrInicial(totalVlrInicial);


			//ValorNR  
			BigDecimal sumValorNrEstornoS = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroNR.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.TRUE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal sumValorNrEstornoN = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroNR.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.FALSE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal valorNr = sumValorNrEstornoN.subtract(sumValorNrEstornoS);
			relatorioMensalMaterialVO.setVlrNr(valorNr);
			totalVlrNr = totalVlrNr.add(valorNr);
			relatorioMensalMaterialVO.setTotalVlrNr(totalVlrNr);

			//ValorDA 
			BigDecimal sumValorDaEstornoS = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroDA.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.TRUE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal sumValorDaEstornoN = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroDA.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.FALSE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal valorDA = sumValorDaEstornoN.subtract(sumValorDaEstornoS);
			relatorioMensalMaterialVO.setVlrDa(valorDA);
			totalVlrDa = totalVlrDa.add(valorDA).setScale(2, RoundingMode.HALF_UP);
			relatorioMensalMaterialVO.setTotalVlrDa(totalVlrDa);

			//ValorRM  
			BigDecimal sumValorRMEstornoS = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroRM.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.TRUE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal sumValorRMEstornoN = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroRM.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.FALSE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal valorRM = sumValorRMEstornoN.subtract(sumValorRMEstornoS).setScale(2, RoundingMode.HALF_UP);
			relatorioMensalMaterialVO.setVlrRm(valorRM);
			totalVlrRm = totalVlrRm.add(valorRM).setScale(2, RoundingMode.HALF_UP);
			relatorioMensalMaterialVO.setTotalVlrRm(totalVlrRm);

			//ValorDF  
			BigDecimal sumValorDFEstornoS = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroDF.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.TRUE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal sumValorDFEstornoN = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroDF.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.FALSE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal valorDF = sumValorDFEstornoN.subtract(sumValorDFEstornoS);
			relatorioMensalMaterialVO.setVlrDf(valorDF);
			totalVlrDf = totalVlrDf.add(valorDF);
			relatorioMensalMaterialVO.setTotalVlrDf(totalVlrDf);

			//ValorAjste  
			BigDecimal sumValorAjstEstornoS = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroPOS.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.TRUE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal sumValorAjstEstornoN = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroPOS.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.FALSE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal valorAjste = sumValorAjstEstornoN.subtract(sumValorAjstEstornoS);
			relatorioMensalMaterialVO.setVlrAjste(valorAjste);
			totalVlrAjste = totalVlrAjste.add(valorAjste);
			relatorioMensalMaterialVO.setTotalVlrAjste(totalVlrAjste);

			//ValorAjsts  
			BigDecimal sumValorAjstsEstornoS = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroNEG.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.TRUE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal sumValorAjstsEstornoN = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroNEG.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.FALSE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal valorAjsts = sumValorAjstsEstornoN.subtract(sumValorAjstsEstornoS);
			relatorioMensalMaterialVO.setVlrAjsts(valorAjsts);
			totalVlrAjsts = totalVlrAjsts.add(valorAjsts);
			relatorioMensalMaterialVO.setTotalVlrAjsts(totalVlrAjsts);

			//ValorPI 
			BigDecimal sumValorPIEstornoS = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroPI.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.TRUE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal sumValorPIEstornoN = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroPI.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.FALSE, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal valorPI = sumValorPIEstornoN.subtract(sumValorPIEstornoS);
			relatorioMensalMaterialVO.setVlrPi(valorPI);
			totalVlrPi = totalVlrPi.add(valorPI);
			relatorioMensalMaterialVO.setTotalVlrPi(totalVlrPi);

			//valorOutrosEntrada  
			oEntrada = true;//Parâmetro para reaproveitamento da query para valor outros entrada/saída
			BigDecimal outrosEntradaEstornoS = getSceMovimentoMaterialDAO().pesquisarValorOutros(dataCompetencia,parametroDocTr.getVlrNumerico().shortValue(),parametroDocTrCompl.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.TRUE, oEntrada, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal outrosEntradaEstornoN = getSceMovimentoMaterialDAO().pesquisarValorOutros(dataCompetencia,parametroDocTr.getVlrNumerico().shortValue(),parametroDocTrCompl.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.FALSE, oEntrada, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal valorOutrosEntrada = outrosEntradaEstornoN.subtract(outrosEntradaEstornoS);
			relatorioMensalMaterialVO.setOutrosEntrada(valorOutrosEntrada);
			totalOutrosEntrada = totalOutrosEntrada.add(valorOutrosEntrada);
			relatorioMensalMaterialVO.setTotalOutrosEntrada(totalOutrosEntrada);

			//valorOutrosSaida  
			oEntrada = false; 
			BigDecimal outrosSaidaEstornoS = getSceMovimentoMaterialDAO().pesquisarValorOutros(dataCompetencia,parametroDocTrCompl.getVlrNumerico().shortValue(),parametroDocTr.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.TRUE ,oEntrada, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal outrosSaidaEstornoN = getSceMovimentoMaterialDAO().pesquisarValorOutros(dataCompetencia,parametroDocTrCompl.getVlrNumerico().shortValue(),parametroDocTr.getVlrNumerico().shortValue(), grupoMaterial.getCodigo(), Boolean.FALSE,oEntrada, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal valorOutrosSaida = outrosSaidaEstornoN.subtract(outrosSaidaEstornoS);
			relatorioMensalMaterialVO.setOutrosSaida(valorOutrosSaida);
			totalOutrosSaida = totalOutrosSaida.add(valorOutrosSaida);
			relatorioMensalMaterialVO.setTotalOutrosSaida(totalOutrosSaida);

			//ValorAtual
			BigDecimal valorAtual = getSceEstoqueGeralDAO().pesquisarSomaValorEstocGeral(dataCompetencia, grupoMaterial.getCodigo(), parametroHcpa.getVlrNumerico().intValue());
			relatorioMensalMaterialVO.setVlrAtual(valorAtual);
			totalVlrAtual = totalVlrAtual.add(valorAtual).setScale(2, RoundingMode.HALF_UP);
			relatorioMensalMaterialVO.setTotalVlrAtual(totalVlrAtual);

			//RETURN( nvl(:VALOR_ATUAL/:cp_tot_val_atual*100, 0) );
			BigDecimal cpTotalvalorAtual = getSceEstoqueGeralDAO().pesquisarSomaValorEstocGeral(dataCompetencia, null, parametroHcpa.getVlrNumerico().intValue());
			BigDecimal totalValor = BigDecimal.valueOf(valorAtual.doubleValue() / cpTotalvalorAtual.doubleValue()*100).setScale(2, RoundingMode.HALF_UP);
			relatorioMensalMaterialVO.setTotValor(decimalFormat.format(totalValor.doubleValue()));  
	
			BigDecimal cpTotValorRmS = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroRM.getVlrNumerico().shortValue(),null, Boolean.TRUE, null);
			BigDecimal cpTotValorRmN = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroRM.getVlrNumerico().shortValue(),null, Boolean.FALSE, null);
			cpTotValorRm = cpTotValorRmN.subtract(cpTotValorRmS).setScale(2, RoundingMode.HALF_UP);
			
			BigDecimal cpTotValorDaS = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroDA.getVlrNumerico().shortValue(),null, Boolean.TRUE, null);
			BigDecimal cpTotValorDaN = getSceMovimentoMaterialDAO().pesquisarValorNrRelMensalMateriais(dataCompetencia, parametroDA.getVlrNumerico().shortValue(),null, Boolean.FALSE, null);
			cpTotValorDa = cpTotValorDaN.subtract(cpTotValorDaS).setScale(2, RoundingMode.HALF_UP);
			
			BigDecimal totalConsumo;

			if(cpTotValorRm.equals(bDzero) || cpTotValorDa.equals(bDzero) ){
				totalConsumo = bDzero;
			}else{
				//n( (nvl(:valor_rm,0)-nvl(:valor_da,0))* 100 /(:cp_tot_valor_rm - :cp_tot_valor_da) );
				totalConsumo = BigDecimal.valueOf((valorRM.doubleValue() - valorDA.doubleValue()) * 100  / (cpTotValorRm.doubleValue() - cpTotValorDa.doubleValue() )).setScale(2, RoundingMode.HALF_UP);
			}
			relatorioMensalMaterialVO.setTotCons(decimalFormat.format(totalConsumo.doubleValue()));

			//RETURN(:CS_VALOR_ATUAL/:CS_VALOR_RM*30);
			BigDecimal cobDias;
			if(valorRM.equals(bDzero)){
				cobDias = bDzero;
			}else{
				cobDias = BigDecimal.valueOf(valorAtual.doubleValue() /  valorRM.doubleValue() * 30).setScale(2, RoundingMode.HALF_UP);
			}
			if(totalVlrRm.equals(bDzero)){
				totalCobDias = bDzero;
			} else {
				totalCobDias =  BigDecimal.valueOf(totalVlrAtual.doubleValue() /  totalVlrRm.doubleValue() * 30).setScale(2, RoundingMode.HALF_UP);
			}
			relatorioMensalMaterialVO.setCobertDias(decimalFormat.format(cobDias));

			relatorioMensalMaterialVO.setTotalCobertDias(decimalFormat.format(totalCobDias));

			BigDecimal fechamentoMensal = totalVlrInicial.add(totalVlrNr).add(totalVlrDa).subtract(totalVlrRm).subtract(totalVlrDf).add(totalVlrAjste).subtract(totalVlrAjsts).add(totalVlrPi).add(totalOutrosEntrada).subtract(totalOutrosSaida).setScale(2, RoundingMode.HALF_UP); 
			if(fechamentoMensal.subtract(totalVlrAtual).equals(bDzero)){
				fechamento = "Fech OK";
			}else{
				fechamento = "Fech Não OK";
			}

			relatorioMensalMaterialVO.setFechamentoMensal(fechamento);
			
			/*
			 * Verificar se código SIAF
			 * 
			 * #40303 - Relatório RG não muda valores ao trocar Agrupamento.
			 * 
			 */

			if(agrupar.equals(DominioAgruparRelMensal.CODIGO_NATUREZA_SIAFI)){
				relatorioMensalMaterialVO.setCodigo(grupoMaterial.getNtdCodigo());
			}

			listRelMensalMaterial.add(relatorioMensalMaterialVO);
		}

		return 	listRelMensalMaterial;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}


}
