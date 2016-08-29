package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.dominio.DominioUnidadeIdade;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.model.AfaDiluicaoMdto;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MptParamCalculoDoses;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentosDia;
import br.gov.mec.aghu.prescricaomedica.dao.VMpmDosagemDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptParamCalculoDosesDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloItemMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDiaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AfaFormaDosagemVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MedicamentosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ParametroDoseUnidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloItensMedicamentoVO;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class CadastroSolucaoProtocoloRN extends BaseBusiness{


	/**
	 * @author marcelo.deus
	 * #44281
	 */
	private static final long serialVersionUID = -3431138948934388316L;
	
	private static final Log LOG = LogFactory.getLog(CadastroSolucaoProtocoloRN.class);
	
	private static final String P_UNIDADE_ML = "P_UNIDADE_ML";

	@Inject
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@Inject
	private IParametroFacade parametroFacade;
	
	@Inject 
	private AfaFormaDosagemDAO afaFormaDosagemDAO;

	@Inject 
	private VMpmDosagemDAO vMpmDosagemDAO;

	@Inject 
	private MptProtocoloItemMedicamentosDAO mptProtocoloItemMedicamentosDAO;

	@Inject 
	private MptProtocoloMedicamentosDAO mptProtocoloMedicamentosDAO;
	
	@Inject 
	private MptProtocoloMedicamentosDiaDAO mptProtocoloMedicamentosDiaDAO;

	@Inject
	private MptParamCalculoDosesDAO mptParamCalculoDosesDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private BigDecimal defaultMaximo = new BigDecimal("999.000");
	private BigDecimal defaultMinimo = new BigDecimal("0.000");
	private BigDecimal defaultMaximoSemZero = new BigDecimal("999");
	private BigDecimal defaultMinimoSemZero = BigDecimal.ZERO;
	
	public enum CadastroSolucaoProtocoloRNExceptionCode implements BusinessExceptionCode {
		MS01_MPT_PROT_SOL_MED_EXISTENTE, MS02_MPT_PROT_SOL_ADD_MED, MS03_MPT_PROT_SOL_OBRIG_FREQ, MS04_MPT_PROT_SOL_OBRIG_UNIDTEMPO, MS05_MPT_PROT_SOL_OBRIG_CORRER_EM,
		MS06_MPT_PROT_SOL_OBRIG_UNIDINFUS, MS07_MPT_PROT_SOL_OBRIG_VELOCINFUS, MS08_MPT_PROT_SOL_INSERIDO_SUCESSO, MS09_MPT_PROT_SOL_EXCLUIDO_SUCESSO, CAMPO_TIPO_CALCULO_NULO,
		CAMPO_DOSE_UNITARIA_NULO, CAMPO_UNIDADE_NULO, CAMPO_UNIDADE_IDADE_NULO, PESO_IDADE_NAO_PERMITIDO, FAIXAS_SOBREPOSTAS, 
		CAMPO_MEDICAMENTO_NULO,	CAMPO_DESCRICAO_NULO,CAMPO_DOSE_NULO,CAMPO_VIA_NULO,CAMPO_APRAZAMENTO_NULO,	LISTA_PARAMETRO_DOSE_MEDICAMENTO_VAZIA,
		CAMPO_UNIDADE_DE_TEMPO_NULO,CAMPO_VELOCIDADE_DE_INFUSAO_NULO,CAMPO_UNIDADE_DE_INFUSAO_NULO, CAMPO_COMPLEMENTO_NULO, MS10_MPT_PROT_SOL_N_OBRIG_FREQ, MSG_CAMPO_FREQUENCIA_MEDICAMENTO,
		MSG03_PESO_IDADE_MAX_MENOR_MIN, MSG_MEDICAMENTO_NAO_ASSOCIADO_DOSE;
	}
	
	/**
	 * Procedure responsável por retornar o valor do volume(ml) de cada medicamento.
	 * 
	 * @param protocoloItensMedicamentoVO
	 * @return
	 */
	public BigDecimal obterVolumeMlMedicamento(ProtocoloItensMedicamentoVO item){

		BigDecimal vQtdePreparadaConv = BigDecimal.ZERO;
		BigDecimal vQtdePreparada = BigDecimal.ZERO;
		
		if (item != null) {
			
			AfaFormaDosagem formaDosagem = afaFormaDosagemDAO.buscarDosagemPadraoMedicamentoMedida(item.getMedMatCodigo(), item.getDosagem().getSeqDosagem());
			AghParametros curGetParam =  parametroFacade.obterAghParametroPorNome(P_UNIDADE_ML);
			VMpmDosagem curFds = procedimentoTerapeuticoFacade.buscarMpmDosagemPorSeqMedicamentoSeqDosagem(item.getMedMatCodigo(), formaDosagem.getSeq());
			
			if (curFds != null && curFds.getFdsUmmSeq() != null && (curGetParam.getVlrNumerico().intValue() == curFds.getFdsUmmSeq().intValue())) {
				return item.getPimDose();
			}
			
			AfaDiluicaoMdto curDil = procedimentoTerapeuticoFacade.buscarAfaDiluicaoMdtoPorMatCodigoEData(item.getMedMatCodigo(), new Date());
			
			if (curDil == null) {
				return BigDecimal.ZERO;
			}
			
			if (curDil.getAfaFormaDosagem() != null && formaDosagem != null && curDil.getAfaFormaDosagem().getSeq() != formaDosagem.getSeq()) {
				
				vQtdePreparadaConv = item.getPimDose().divide(curFds.getFormaDosagem().getFatorConversaoUp(), RoundingMode.HALF_EVEN);
				
				curFds = procedimentoTerapeuticoFacade.buscarMpmDosagemPorSeqMedicamentoSeqDosagem(item.getMedMatCodigo(), curDil.getAfaFormaDosagem().getSeq());
				
				vQtdePreparada = vQtdePreparadaConv.multiply(curFds.getFormaDosagem().getFatorConversaoUp()); 
				
			} else {
				vQtdePreparada = item.getPimDose();
			}
			return vQtdePreparada.multiply(new BigDecimal(curDil.getQtdeMl())).divide(new BigDecimal(curDil.getQtdeUnidade()), RoundingMode.HALF_EVEN);
		}
		return BigDecimal.ZERO;
	}

	public void verificarInsercaoSolucao(MptProtocoloMedicamentos solucao,	List<ProtocoloItensMedicamentoVO> listaMedicamentos) throws ApplicationBusinessException {
		
		verificaTamanhoLista(listaMedicamentos);
		
		verificaTfqEFrequencia(solucao);
		
		verificaQtdHorasEUnidHoras(solucao);
		
		if(listaMedicamentos != null && !listaMedicamentos.isEmpty()){
			for(ProtocoloItensMedicamentoVO medicamento : listaMedicamentos){
				if(medicamento.getListaParametroCalculo().isEmpty()){
					List<ParametroDoseUnidadeVO> listaTemp = mptParamCalculoDosesDAO.listarParametroDoseMedicamento(medicamento);
					if(listaTemp != null){
						medicamento.getListaParametroCalculo().addAll(listaTemp);
					}
				}
				if(medicamento.getListaParametroCalculo() != null && medicamento.getListaParametroCalculo().isEmpty()){
					throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MSG_MEDICAMENTO_NAO_ASSOCIADO_DOSE);
				}
			}
		}
		
		if(solucao != null &&  solucao.getUnidHorasCorrer() != null && solucao.getQtdeHorasCorrer() == null){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MS05_MPT_PROT_SOL_OBRIG_CORRER_EM);
		}

		if(solucao != null &&  solucao.getGotejo() != null && solucao.getTipoVelocAdministracoes() == null){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MS06_MPT_PROT_SOL_OBRIG_UNIDINFUS);
		}

		if(solucao != null &&  solucao.getTipoVelocAdministracoes() != null && solucao.getGotejo() == null){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MS07_MPT_PROT_SOL_OBRIG_VELOCINFUS);
		}
	}

	private void verificaQtdHorasEUnidHoras(MptProtocoloMedicamentos solucao)
			throws ApplicationBusinessException {
		if(solucao != null && solucao.getQtdeHorasCorrer() != null && solucao.getUnidHorasCorrer() == null){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MS04_MPT_PROT_SOL_OBRIG_UNIDTEMPO);
		}
	}

	private void verificaTfqEFrequencia(MptProtocoloMedicamentos solucao)
			throws ApplicationBusinessException {
		if((solucao != null && solucao.getTfqSeq() != null && solucao.getTfqSeq().getIndDigitaFrequencia()) && (solucao != null && (solucao.getFrequencia() == null || solucao.getFrequencia() == 0))){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MS03_MPT_PROT_SOL_OBRIG_FREQ);
		}
		
		if((solucao != null && solucao.getTfqSeq() != null && !solucao.getTfqSeq().getIndDigitaFrequencia()) && (solucao != null && solucao.getFrequencia() != null )){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MS10_MPT_PROT_SOL_N_OBRIG_FREQ);
		}
	}

	private void verificaTamanhoLista(
			List<ProtocoloItensMedicamentoVO> listaMedicamentos)
			throws ApplicationBusinessException {
		if(listaMedicamentos != null && listaMedicamentos.isEmpty()){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MS02_MPT_PROT_SOL_ADD_MED);
		}
	}

	public void excluirItemMedicamento(Long ptmSeq) {
		List<MptProtocoloItemMedicamentos> itensMedicamento = mptProtocoloItemMedicamentosDAO.obterItensMedicamentoPorSeqProtocolo(ptmSeq);
		
		List<MptProtocoloMedicamentosDia> itensMedicamentoDia = mptProtocoloMedicamentosDiaDAO.verificarExisteDiaMarcadoParaProtocolo(ptmSeq);
		
		
		if(itensMedicamentoDia != null && !itensMedicamentoDia.isEmpty()){
			for(MptProtocoloMedicamentosDia dia : itensMedicamentoDia){
				mptProtocoloMedicamentosDiaDAO.remover(dia);
			}
		}
		
		for(MptProtocoloItemMedicamentos obj : itensMedicamento){
			List<MptParamCalculoDoses> itemParamDose = mptParamCalculoDosesDAO.obterListaDoseMdtosPorSeqMdto(obj.getSeq());
			for(MptParamCalculoDoses item : itemParamDose){
				mptParamCalculoDosesDAO.remover(item);
			}
			mptProtocoloItemMedicamentosDAO.remover(obj);
		}
		mptProtocoloMedicamentosDAO.removerPorId(ptmSeq);
	}

	public List<ProtocoloItensMedicamentoVO> listarItensMedicamento(Long codSolucao) {
		List<ProtocoloItensMedicamentoVO> lista = mptProtocoloItemMedicamentosDAO.listarItensMedicamento(codSolucao);
		
		for(ProtocoloItensMedicamentoVO obj : lista){
			VMpmDosagem dosagem = vMpmDosagemDAO.buscarMpmDosagemPorSeqDosagem(obj.getFdsSeq()); 
			obj.setDosagem(dosagem);
		}
		return lista;
	}

	public void validarCamposObrigatoriosParametroDose(ParametroDoseUnidadeVO paramDose, Boolean desabilitarCampoUnidade) throws BaseListException {
		if(paramDose != null){
			
			BaseListException listaDeErros = new BaseListException();
			
			if(paramDose.getTipoCalculo() == null){
				listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_TIPO_CALCULO_NULO, Severity.ERROR));	
			}
			
			if(paramDose.getDose() == null){
				listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_DOSE_UNITARIA_NULO, Severity.ERROR));
			}
			
			if(paramDose.getComboUnidade() == null && paramDose.getAfaFormaDosagemVO() == null && !desabilitarCampoUnidade){
				listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_UNIDADE_NULO, Severity.ERROR));
			}
			
			if( paramDose.getUnidIdade() == null && (paramDose.getIdadeMaxima() != null || paramDose.getIdadeMinima() != null)){
				listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_UNIDADE_IDADE_NULO, Severity.ERROR));
			}
			if(listaDeErros.hasException()){
				throw listaDeErros;
			}
		}
		
	}

	public Boolean validarAdicaoParametroDoseParteUm(List<ParametroDoseUnidadeVO> listaParam,	ParametroDoseUnidadeVO paramDoseVO) {
		/*
		 * 1 – Dose Fixa
			Se o usuário informar um cálculo(um registro na grid de cálculo) sem informar o 
			range de idade ou peso, somente esta linha deve ser inserida para o mesmo, não 
			permitindo mais linhas para este medicamento e desabilitando o botão adicionar.
		 */
		
		Boolean validarLista = Boolean.FALSE;
		
		if(paramDoseVO.getIdadeMaxima() == null && paramDoseVO.getIdadeMinima() == null && 
				paramDoseVO.getPesoMaximo() == null && paramDoseVO.getPesoMinimo() == null){
			
			if(listaParam.isEmpty()){
				return false;

			} else{
				for(ParametroDoseUnidadeVO obj : listaParam){
					if(obj.getIdadeMaxima() == null && obj.getIdadeMinima() == null && 
							obj.getPesoMaximo() == null && obj.getPesoMinimo() == null){
						validarLista = Boolean.TRUE;
					} 
//					else {
//						validarLista = false;
//					}
				}
				return validarLista;
			}
		} else {
			return false;
		}
	}

	public void validarPesoOuIdadeParamDose(List<ParametroDoseUnidadeVO> listaParam, ParametroDoseUnidadeVO paramDoseVO) throws ApplicationBusinessException {
		/*
		 * 2 – Peso ou Idade
			O usuário só poderá informar uma configuração por peso(peso mínimo e peso máximo) 
			ou por idade(idade mínima ou idade máxima), ou seja caso a primeira linha inserida 
			leve em consideração um range de peso, as outras linhas obrigatoriamente também só 
			poderão aceitar inserções que levem em consideração o parâmetro de peso, a mesma 
			regra se aplica para a idade. Caso o usuário tente informar os dois tipos de 
			configuração, não permitir e exibir a MSG01.
		 */
		if(listaParam == null || listaParam.isEmpty()){
			validarParametrosInsercao(paramDoseVO);
		} else  if(!listaParam.isEmpty()){
			validarParametrosInsercaoListagem(listaParam, paramDoseVO);
		}
	}

	private void validarParametrosInsercaoListagem(List<ParametroDoseUnidadeVO> listaParam, ParametroDoseUnidadeVO paramDoseVO) throws ApplicationBusinessException {
		for(ParametroDoseUnidadeVO obj : listaParam){
			if (!obj.getSeq().equals(paramDoseVO.getSeq())) {
				if((obj.getIdadeMaxima() != null || obj.getIdadeMinima() != null) && 
						(paramDoseVO.getPesoMaximo() != null || paramDoseVO.getPesoMinimo() != null)){
					throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.PESO_IDADE_NAO_PERMITIDO, Severity.ERROR);
				} else if((obj.getPesoMaximo() != null || obj.getPesoMinimo() != null) &&
						(paramDoseVO.getIdadeMaxima() != null && paramDoseVO.getIdadeMinima() != null)){
					throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.PESO_IDADE_NAO_PERMITIDO, Severity.ERROR);
				} else if((obj.getPesoMaximo() == null && obj.getPesoMinimo() == null && obj.getIdadeMaxima() == null && obj.getIdadeMinima() == null)
						&& ((paramDoseVO.getIdadeMaxima() != null || paramDoseVO.getIdadeMinima() != null) 
								&& (paramDoseVO.getPesoMaximo() != null || paramDoseVO.getPesoMinimo() != null))){
					throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.PESO_IDADE_NAO_PERMITIDO, Severity.ERROR);
				}
			} else {
				validarParametrosInsercao(paramDoseVO);
			}
		}
	}

	private void validarParametrosInsercao(ParametroDoseUnidadeVO paramDoseVO)
			throws ApplicationBusinessException {
		if((paramDoseVO.getIdadeMaxima() != null || paramDoseVO.getIdadeMinima() != null) && 
				(paramDoseVO.getPesoMaximo() != null || paramDoseVO.getPesoMinimo() != null)){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.PESO_IDADE_NAO_PERMITIDO, Severity.ERROR);
		}
	}

	public void validarSobreposicaoDeFaixaParamDose(List<ParametroDoseUnidadeVO> listaParam, ParametroDoseUnidadeVO paramDoseVO) throws ApplicationBusinessException{
		/*
		 * 3 – Sobreposições de faixas
			As faixas configuradas por peso ou idade, não devem permitir sobreposições das mesmas, por exemplo:
			Se for informado uma faixa de peso de 0 a 70Kg não deve permitir uma outra configuração que sobreponha 
			esta, por exemplo de 0Kg a 50Kg, o mesmo vale para a idade, caso ocorra exibir a MSG02
		 */
		
		if((!listaParam.isEmpty() && !paramDoseVO.getIsEdicao()) || (paramDoseVO.getIsEdicao() && listaParam.size() > 1)){
			for(ParametroDoseUnidadeVO obj : listaParam){
				if (!obj.getSeq().equals(paramDoseVO.getSeq())) {
					
					Integer valorMin = null;
					Integer valorMax = null;
					Integer valorMinComparacao = null;
					Integer valorMaxComparacao = null;
					
					if (paramDoseVO.getPesoMaximo() != null || paramDoseVO.getPesoMinimo() != null) {
						if (paramDoseVO.getPesoMinimo() != null) {
							valorMin = obterValorMinimoPeso(paramDoseVO);
						}
						if (paramDoseVO.getPesoMaximo() != null) {
							valorMax = obterValorMaximoPeso(paramDoseVO);
						}
						if (obj.getPesoMinimo() != null) {
							valorMinComparacao = obterValorMinimoPeso(obj);
						}
						if (obj.getPesoMaximo() != null) {
							valorMaxComparacao = obterValorMaximoPeso(obj);
						}
					} else {
						if (DominioUnidadeIdade.M.equals(paramDoseVO.getUnidIdade())) {
							valorMin = obterValorMinimo(paramDoseVO);
							valorMax = obterValorMaximo(paramDoseVO);
						} else {
							valorMin = obterValorMinimoEmMeses(paramDoseVO);
							valorMax = obterValorMaximoEmMeses(paramDoseVO);
						}
						
						if (DominioUnidadeIdade.M.equals(obj.getUnidIdade())) {
							valorMinComparacao = obterValorMinimo(obj);
							valorMaxComparacao = obterValorMaximo(obj);
						} else {
							valorMinComparacao = obterValorMinimoEmMeses(obj);
							valorMaxComparacao = obterValorMaximoEmMeses(obj);
						}
					}
					
					verificarFaixas(valorMin, valorMax, valorMinComparacao, valorMaxComparacao); 
				}
			}
		}
	}

	private Integer obterValorMinimo(ParametroDoseUnidadeVO paramDoseVO) {
		if (paramDoseVO.getIdadeMinima() != null) {
			return Integer.valueOf(paramDoseVO.getIdadeMinima());
		}
		return null;
	}

	private Integer obterValorMinimoPeso(ParametroDoseUnidadeVO paramDoseVO) {
		if (paramDoseVO.getPesoMinimo() != null) {
			return paramDoseVO.getPesoMinimo().multiply(new BigDecimal(1000)).intValue();
		}
		return null;
	}
	

	private Integer obterValorMaximoPeso(ParametroDoseUnidadeVO paramDoseVO) {
		if (paramDoseVO.getPesoMaximo() != null) {
			return paramDoseVO.getPesoMaximo().multiply(new BigDecimal(1000)).intValue();
		}
		return null;
	}
	

	private Integer obterValorMinimoEmMeses(ParametroDoseUnidadeVO paramDoseVO) {
		Integer valor = obterValorMinimo(paramDoseVO);
		if (valor != null) {
			return valor * 12;
		}
		return null;
	}
	
	private Integer obterValorMaximo(ParametroDoseUnidadeVO paramDoseVO) {
		if (paramDoseVO.getIdadeMaxima() != null) {
			return Integer.valueOf(paramDoseVO.getIdadeMaxima());
		}
		return null;
	}

	private Integer obterValorMaximoEmMeses(ParametroDoseUnidadeVO paramDoseVO) {
		Integer valor = obterValorMaximo(paramDoseVO);
		if (valor != null) {
			return valor * 12;
		}
		return null;
	}

	private void verificarFaixas(Integer valorMin, Integer valorMax, Integer valorMinComparacao, Integer valorMaxComparacao) throws ApplicationBusinessException {
		if (valorMaxComparacao != null && valorMinComparacao != null) {
			verificarAmbasFaixas(valorMin, valorMax, valorMinComparacao, valorMaxComparacao);
		} else if (valorMaxComparacao != null)  {
			verificarFaixaMinima(valorMin, valorMax, valorMaxComparacao);
		} else if (valorMinComparacao != null) {
			verificarFaixaMaxima(valorMin, valorMax, valorMinComparacao);
		}
	}

	private void verificarFaixaMaxima(Integer valorMin, Integer valorMax, Integer valorMinComparacao) throws ApplicationBusinessException {
		if ((valorMax == null && valorMin != null) || (valorMax != null && valorMax >= valorMinComparacao)) {
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.FAIXAS_SOBREPOSTAS, Severity.ERROR);
		}
	}

	private void verificarFaixaMinima(Integer valorMin, Integer valorMax, Integer valorMaxComparacao) throws ApplicationBusinessException {
		if ((valorMin == null && valorMax != null) || (valorMin != null && valorMin <= valorMaxComparacao)) {
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.FAIXAS_SOBREPOSTAS, Severity.ERROR);
		}
	}

	private void verificarAmbasFaixas(Integer valorMin, Integer valorMax, Integer valorMinComparacao, Integer valorMaxComparacao) throws ApplicationBusinessException {
		
		if(valorMin != null && valorMax != null && (CoreUtil.isBetweenRange(valorMin, valorMinComparacao, valorMaxComparacao) ||
				CoreUtil.isBetweenRange(valorMin, 0, valorMaxComparacao) ||
				CoreUtil.isBetweenRange(valorMax, valorMinComparacao , valorMaxComparacao))){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.FAIXAS_SOBREPOSTAS, Severity.ERROR);
		} else if(valorMin != null && valorMax == null && CoreUtil.isBetweenRange(valorMin, 0, valorMaxComparacao)){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.FAIXAS_SOBREPOSTAS, Severity.ERROR);
		} else if(valorMax != null && valorMin == null && CoreUtil.isBetweenRange(valorMax, valorMinComparacao, 999999)){
			throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.FAIXAS_SOBREPOSTAS, Severity.ERROR);
		}
//			if ((valorMin != null && valorMin <= valorMaxComparacao) || (valorMax != null && valorMax >= valorMinComparacao)) {
//				throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.FAIXAS_SOBREPOSTAS, Severity.ERROR);
//			}
	}
	
	public List<ParametroDoseUnidadeVO> preCarregarListaParametroDoseMedicamentoSolucao(ProtocoloItensMedicamentoVO medicamentoSelecionado) {
		
		List<ParametroDoseUnidadeVO> lista = mptParamCalculoDosesDAO.listarParametroDoseMedicamento(medicamentoSelecionado);
		VMpmDosagem dosagem = new VMpmDosagem();
		for(ParametroDoseUnidadeVO obj : lista){
			dosagem = vMpmDosagemDAO.buscarMpmDosagemPorSeqDosagem(obj.getFdsSeq());
			if(dosagem == null){
				if(obj.getAfaFormaDosagem() != null){
					dosagem = vMpmDosagemDAO.buscarMpmDosagemPorSeqDosagem(obj.getAfaFormaDosagem().getSeq());
				}
			} 
			if(dosagem != null){
				obj.setComboUnidade(dosagem);
				AfaFormaDosagemVO dosagemVO = new AfaFormaDosagemVO();
				
				dosagemVO.setUmmDescricao(obj.getComboUnidade().getSeqUnidade());
				obj.setAfaFormaDosagemVO(dosagemVO);
			}
			if(obj.getIdadeMaxima() == 999){
				obj.setIdadeMaxima(null);
			}
			if(obj.getIdadeMinima() == 0){
				obj.setIdadeMinima(null);
			}
			if(defaultMaximo.equals(obj.getPesoMaximo()) || defaultMaximoSemZero.equals(obj.getPesoMaximo())){
				obj.setPesoMaximo(null);
			}
			if(defaultMinimo.equals(obj.getPesoMinimo()) || defaultMinimoSemZero.equals(obj.getPesoMinimo())){
				obj.setPesoMinimo(null);
			}
		}
		return lista;
	}

	public void preInserirParametroDoseMedicamentoSolucao(MptParamCalculoDoses calculoDose) {
		if (calculoDose.getIdadeMaxima() == null){
			calculoDose.setIdadeMaxima(Short.valueOf("999"));
		}
		if(calculoDose.getIdadeMinima() == null){
			calculoDose.setIdadeMinima(Short.valueOf("0"));
		}
		if(calculoDose.getPesoMaximo() == null){
			calculoDose.setPesoMaximo(defaultMaximo);
		}
		if(calculoDose.getPesoMinimo() == null){
			calculoDose.setPesoMinimo(defaultMinimo);
		}
	}

	public BigDecimal getDefaultMaximo() {
		return defaultMaximo;
	}

	public void setDefaultMaximo(BigDecimal defaultMaximo) {
		this.defaultMaximo = defaultMaximo;
	}

	public BigDecimal getDefaultMinimo() {
		return defaultMinimo;
	}

	public void setDefaultMinimo(BigDecimal defaultMinimo) {
		this.defaultMinimo = defaultMinimo;
	}

	public List<ParametroDoseUnidadeVO> preCarregarListaParametroDoseMedicamento(Integer medMatCodigo, Long seqItemProtocoloMdtos) {
		
		ProtocoloItensMedicamentoVO param = new ProtocoloItensMedicamentoVO();
		param.setMedMatCodigo(medMatCodigo);
		param.setPimSeq(seqItemProtocoloMdtos);
		
		List<ParametroDoseUnidadeVO> lista = mptParamCalculoDosesDAO.listarParametroDoseMedicamento(param);
		
		AfaFormaDosagemVO dosagem = new AfaFormaDosagemVO();
		for(ParametroDoseUnidadeVO obj : lista){
			if(obj.getFdsSeq() != null){
				dosagem = procedimentoTerapeuticoFacade.obterFormaDosagem(obj.getFdsSeq()); 
			} else {
				if(obj.getAfaFormaDosagem() != null){
					dosagem = procedimentoTerapeuticoFacade.obterFormaDosagem(obj.getAfaFormaDosagem().getSeq());
				}
			}
			obj.setAfaFormaDosagemVO(dosagem);
			
			if(obj.getIdadeMaxima() == 999){
				obj.setIdadeMaxima(null);
			}
			if(obj.getIdadeMinima() == 0){
				obj.setIdadeMinima(null);
			}
			if(defaultMaximo.equals(obj.getPesoMaximo()) || defaultMaximoSemZero.equals(obj.getPesoMaximo())){
				obj.setPesoMaximo(null);
			}
			if(defaultMinimo.equals(obj.getPesoMinimo()) || defaultMinimoSemZero.equals(obj.getPesoMinimo())){
				obj.setPesoMinimo(null);
			}
		}
		return lista;
	}

	public void removerParametroDose(ParametroDoseUnidadeVO item) {
		MptParamCalculoDoses parametroDose = mptParamCalculoDosesDAO.obterPorChavePrimaria(item.getSeq());
		mptParamCalculoDosesDAO.remover(parametroDose);
	}

	public void validarCamposObrigatoriosMedicamento(MedicamentosVO medicamentosVO,MptProtocoloMedicamentos mptProtocoloMedicamentos, 
			MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos, AfaFormaDosagemVO unidadeSelecionada, List<ParametroDoseUnidadeVO> listaParam,
			AfaViaAdministracao viaSelecionada,	MpmTipoFrequenciaAprazamento aprazamentoSelecionado,DominioUnidadeHorasMinutos unidHorasCorrer,
			AfaTipoVelocAdministracoes unidadeInfusaoSelecionada) throws BaseListException {
		
		BaseListException listaDeErros = new BaseListException();
		
		verificarListaMedicamentoComplementoDescricao(medicamentosVO, mptProtocoloMedicamentos, listaParam, listaDeErros);
	
		verificarDoseUnidadeViaAprazamento(mptProtocoloItemMedicamentos,unidadeSelecionada, viaSelecionada, aprazamentoSelecionado,	listaDeErros);
		
		verificarUniTempoVeloInfusaoUniInfusao(mptProtocoloMedicamentos,unidHorasCorrer, unidadeInfusaoSelecionada, listaDeErros, aprazamentoSelecionado);
		
		if(listaDeErros.hasException()){
			throw listaDeErros;
		}
	}

	private void verificarUniTempoVeloInfusaoUniInfusao(MptProtocoloMedicamentos mptProtocoloMedicamentos,	DominioUnidadeHorasMinutos unidHorasCorrer,
			AfaTipoVelocAdministracoes unidadeInfusaoSelecionada,	BaseListException listaDeErros, MpmTipoFrequenciaAprazamento aprazamentoSelecionado) {
		if(mptProtocoloMedicamentos.getQtdeHorasCorrer() != null && unidHorasCorrer == null ){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_UNIDADE_DE_TEMPO_NULO));
		}
		if(unidadeInfusaoSelecionada != null && mptProtocoloMedicamentos.getGotejo() == null ){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_VELOCIDADE_DE_INFUSAO_NULO));
		}
		if(mptProtocoloMedicamentos.getGotejo() != null && unidadeInfusaoSelecionada == null ){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_UNIDADE_DE_INFUSAO_NULO));
		}
		if(mptProtocoloMedicamentos.getFrequencia() == null && aprazamentoSelecionado != null && aprazamentoSelecionado.getIndDigitaFrequencia()){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MSG_CAMPO_FREQUENCIA_MEDICAMENTO));

		}
	}

	private void verificarDoseUnidadeViaAprazamento(MptProtocoloItemMedicamentos mptProtocoloItemMedicamentos,AfaFormaDosagemVO unidadeSelecionada,	
			AfaViaAdministracao viaSelecionada,	MpmTipoFrequenciaAprazamento aprazamentoSelecionado,BaseListException listaDeErros) {		
		if(mptProtocoloItemMedicamentos.getDose() == null ){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_DOSE_NULO));
		}
		if(unidadeSelecionada == null ){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_UNIDADE_NULO));
		}
		if(viaSelecionada == null ){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_VIA_NULO));
		}
		if(aprazamentoSelecionado == null){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_APRAZAMENTO_NULO));
		}
	}

	private void verificarListaMedicamentoComplementoDescricao(	MedicamentosVO medicamentosVO,	MptProtocoloMedicamentos mptProtocoloMedicamentos,
			List<ParametroDoseUnidadeVO> listaParam, BaseListException listaDeErros) {
		if(listaParam != null && listaParam.isEmpty()){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.LISTA_PARAMETRO_DOSE_MEDICAMENTO_VAZIA));
		}
		if(medicamentosVO == null ){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_MEDICAMENTO_NULO));
		}
		if(medicamentosVO != null && medicamentosVO.getMedIndExigeObservacao() && 
				(mptProtocoloMedicamentos.getComplemento() == null || StringUtils.EMPTY.equals(mptProtocoloMedicamentos.getComplemento()))){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_COMPLEMENTO_NULO));
		}
		if(mptProtocoloMedicamentos.getDescricao() == null || StringUtils.EMPTY.equals(mptProtocoloMedicamentos.getDescricao())){
			listaDeErros.add(new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.CAMPO_DESCRICAO_NULO));
		}
	}

	public void validarPesoOuIdadeMinimoMaiorQueMaximo(ParametroDoseUnidadeVO paramDose) throws ApplicationBusinessException {
		if(paramDose != null){
			if(paramDose.getIdadeMinima() != null && paramDose.getIdadeMaxima() != null && paramDose.getIdadeMinima() > paramDose.getIdadeMaxima()){
				throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MSG03_PESO_IDADE_MAX_MENOR_MIN, Severity.ERROR);
			}
			if(paramDose.getPesoMinimo() != null && paramDose.getPesoMaximo() != null && paramDose.getPesoMinimo().intValue() > paramDose.getPesoMaximo().intValue()){
				throw new ApplicationBusinessException(CadastroSolucaoProtocoloRNExceptionCode.MSG03_PESO_IDADE_MAX_MENOR_MIN, Severity.ERROR);
			}
		}
	}

	public BigDecimal getDefaultMaximoSemZero() {
		return defaultMaximoSemZero;
	}

	public void setDefaultMaximoSemZero(BigDecimal defaultMaximoSemZero) {
		this.defaultMaximoSemZero = defaultMaximoSemZero;
	}

	public BigDecimal getDefaultMinimoSemZero() {
		return defaultMinimoSemZero;
	}

	public void setDefaultMinimoSemZero(BigDecimal defaultMinimoSemZero) {
		this.defaultMinimoSemZero = defaultMinimoSemZero;
	}
}
