package br.gov.mec.aghu.patrimonio;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoProcessamentoMensalVO;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.sig.custos.vo.FolhaPagamentoRHVo;
import br.gov.mec.camel.sispro.EquipamentoDepreciacao;
import br.gov.mec.camel.sispro.EquipamentoDepreciacaoMensal;
import br.gov.mec.camel.sispro.FolhaPagamento;
import br.gov.mec.camel.sispro.InputCodigosPatrimonio;
import br.gov.mec.camel.sispro.InputConsultaPatrimonio;
import br.gov.mec.camel.sispro.InputEquipamentoDepreciacao;
import br.gov.mec.camel.sispro.OutputEquipamentoDepreciacao;
import br.gov.mec.camel.sispro.OutputEquipamentoDepreciacaoMensal;
import br.gov.mec.camel.sispro.OutputFolhaPagamento;
import br.gov.mec.camel.sispro.Patrimonio;
import br.gov.mec.camel.sispro.SisproService;
import br.gov.mec.camel.sispro.SisproServicePortType;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

//@Name("patrimonioService")
// tmp name
@Stateless
public class PatrimonioService implements IPatrimonioService {

	private static final Log LOG = LogFactory.getLog(PatrimonioService.class);
	
	private static final String TAMANHO_PAGINA = "100";

	public enum PatrimonioServiceBusinessExceptionCode implements BusinessExceptionCode{
		ERRO_CONSULTA_PATRIMONIO
	}
	
	@EJB
	private IParametroFacade parametroFacade;
	
	public EquipamentoSistemaPatrimonioVO buscarInfoModuloPatrimonio( String codigoBem, Integer codigoCentroCusto) throws ApplicationBusinessException {
		
		Patrimonio patrimonio = this.buscarPatrimonioComTimeout(codigoBem, codigoCentroCusto);
				
		if(patrimonio == null){
			//Tenta mais uma vez e se não conseguir, então dispara a exception informando problema no serviço de patrimônio
			patrimonio = this.buscarPatrimonioComTimeout(codigoBem, codigoCentroCusto);
			if(patrimonio == null){
				throw new ApplicationBusinessException(PatrimonioServiceBusinessExceptionCode.ERRO_CONSULTA_PATRIMONIO);
			}
		}
		
		EquipamentoSistemaPatrimonioVO result = new EquipamentoSistemaPatrimonioVO();
		result.setCodigo(patrimonio.getCodigo());
		result.setConta(patrimonio.getConta());
		result.setDescricao(patrimonio.getDescricao());
		result.setRetorno(new BigDecimal(patrimonio.getRetorno()));
		result.setValor(patrimonio.getValor());
		result.setValorDepreciado(patrimonio.getValorDepreciacao());

		return result;		
	}
	
	private Patrimonio buscarPatrimonioComTimeout(String codigoBem, Integer codigoCentroCusto) throws ApplicationBusinessException{
		
		final SisproServicePortType port = getServicePort();

		final InputConsultaPatrimonio input = new InputConsultaPatrimonio();
		input.setCodigo(codigoBem);
		input.setCentroCusto(codigoCentroCusto);
		try {
			//As vezes ocorre erro SocketTimeoutException invoking http://aghu-esb-homolog.hcpa:8080/sispro-camel/ws/sisproService: Read timed out
			//Mas esse erro demora mais de 1 minutos para disparar, então coloquei o timeout de no máximo 3 segundos que é tempo suficiente para obter essa resposta
			return Executors.newSingleThreadExecutor().submit( new Callable<Patrimonio>() {
				public Patrimonio call() {
					return port.consultaPatrimonio(input);
				}
			}).get(3, TimeUnit.SECONDS);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentosSistemaPatrimonioById(
			List<String> listCodigo) throws ApplicationBusinessException {
		List<EquipamentoSistemaPatrimonioVO> result = new ArrayList<EquipamentoSistemaPatrimonioVO>();

		if (listCodigo == null || listCodigo.isEmpty()) {
			return result;
		}

		SisproServicePortType port = getServicePort();

		InputCodigosPatrimonio input = new InputCodigosPatrimonio();

		// adaptado para o tipo recebido
		for (String s : listCodigo) {
			input.getList().add(Integer.valueOf(s));
		}

		OutputEquipamentoDepreciacao output = port
				.buscaEquipamentoDepreciacaoByIds(input);
		for (EquipamentoDepreciacao e : output.getList()) {
			EquipamentoSistemaPatrimonioVO vo = new EquipamentoSistemaPatrimonioVO();
			vo.setCodigo(e.getCodigo());
			vo.setConta(e.getConta());
			vo.setDescricao(e.getDescricao());
			vo.setRetorno(BigDecimal.ONE);
			vo.setValor(e.getValor());
			vo.setValorDepreciado(e.getValorDepreciacao());
			result.add(vo);
		}

		return result;
	}

	@Override
	public List<EquipamentoProcessamentoMensalVO> buscaEquipamentosParaProcessamentoMensal(
			String dataProcessamento) throws ApplicationBusinessException {
		SisproServicePortType port = getServicePort();

		// data no formato mm/yyyy
		OutputEquipamentoDepreciacaoMensal depreciacaoMensal = port
				.buscaEquipamentoDepreciacaoMensal(dataProcessamento);
		List<EquipamentoProcessamentoMensalVO> result = new ArrayList<EquipamentoProcessamentoMensalVO>();
		for (EquipamentoDepreciacaoMensal o : depreciacaoMensal.getList()) {
			EquipamentoProcessamentoMensalVO vo = new EquipamentoProcessamentoMensalVO();
			vo.setCodCentroCusto(Integer.parseInt((String) o
					.getCodigoCentroCusto()));
			vo.setBem(o.getCodigoEquipamento());
			vo.setTotalDepreciado(o.getTotalDepreciacao());
			result.add(vo);
		}

		return result;
	}

	@Override
	public List<FolhaPagamentoRHVo> buscaFolhaPagamentoMensal(
			String dataProcessamento) throws ApplicationBusinessException {
		SisproServicePortType port = this.getServicePort();

		// data no formato mm/yyyy
		OutputFolhaPagamento folhaPagamento = port
				.buscaFolhaPagamento(dataProcessamento);
		List<FolhaPagamentoRHVo> result = new ArrayList<FolhaPagamentoRHVo>();
		for (FolhaPagamento o : folhaPagamento.getList()) {
			FolhaPagamentoRHVo vo = new FolhaPagamentoRHVo();
			vo.setCctCodigoAtua(o.getCentroCustoAtua());
			vo.setCctCodigoLotado(o.getCentroCustoLotado());
			vo.setGocSeq(o.getGrupo());
			vo.setOcaCarCodigo(o.getCategoriaCodigo());
			vo.setOcaCodigo(o.getOcupacaoCodigo());
			vo.setNroFuncionarios(o.getNroFuncionarios());
			vo.setTotHrContrato(o.getTotalHorasContrato());
			vo.setTotHrDesconto(o.getTotalHorasDesconto());
			vo.setTotHrExcede(o.getTotHorasExcede());
			vo.setTotSalBase(o.getTotalSalarioBase());
			vo.setTotSalarios(o.getTotalSalarios());
			vo.setTotEncargos(o.getTotalEncargos());
			vo.setTotProv13(o.getTotalProv13());
			vo.setTotProvFerias(o.getTotalProvFerias());
			vo.setTotProvEncargos(o.getTotalProvEncargos());
			result.add(vo);
		}

		return result;
	}

	@Override
	public List<EquipamentoSistemaPatrimonioVO> consultaEquipamentoPelaDescricao(
			String descricao, Integer codigoCentroCusto)
			throws ApplicationBusinessException {
		SisproServicePortType port = getServicePort();

		InputEquipamentoDepreciacao input = new InputEquipamentoDepreciacao();
		input.setCodigoCentroCusto(codigoCentroCusto.toString());
		input.setDescricao("%" + descricao + "%");
		input.setTamanhoPagina(TAMANHO_PAGINA);
		OutputEquipamentoDepreciacao equipamentoDepreciacao = port
				.buscaEquipamentoDepreciacaoByDescricao(input);

		List<EquipamentoSistemaPatrimonioVO> result = new ArrayList<EquipamentoSistemaPatrimonioVO>();

		for (EquipamentoDepreciacao o : equipamentoDepreciacao.getList()) {
			EquipamentoSistemaPatrimonioVO vo = new EquipamentoSistemaPatrimonioVO();
			vo.setConta(o.getConta());
			vo.setCodigo(o.getCodigo());
			vo.setDescricao(o.getDescricao());
			vo.setValor(o.getValor());
			vo.setValorDepreciado(o.getValorDepreciacao());
			vo.setRetorno(BigDecimal.ONE);
			result.add(vo);
		}

		return result;
	}

	@Override
	public List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamentoSistemaPatrimonio(
			Object paramPesquisa, Integer centroCustoAtividade)
			throws ApplicationBusinessException {

		List<EquipamentoSistemaPatrimonioVO> result = new ArrayList<EquipamentoSistemaPatrimonioVO>();

		if (centroCustoAtividade == null) {
			centroCustoAtividade = 0;
		}
		if (CoreUtil.isNumeroInteger(paramPesquisa)) {
			// retorna uma lista com o único retorno
			return Arrays.asList(this.buscarInfoModuloPatrimonio(
					paramPesquisa.toString(), centroCustoAtividade));
		} else {
			result = this.consultaEquipamentoPelaDescricao(
					paramPesquisa.toString(), centroCustoAtividade);
		}

		return result;
	}

	private SisproServicePortType getServicePort()
			throws ApplicationBusinessException {
		SisproService service = new SisproService();
		SisproServicePortType port = service.getSisproServicePort();

		String urlEndpoint = getUrlEndpoint();

		BindingProvider bindingProvider = (BindingProvider) port;
		bindingProvider.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, urlEndpoint);

		return port;
	}

	private String getUrlEndpoint() throws ApplicationBusinessException {
		String result = null;
		try {
			result = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_PATRIMONIO_SERVICE_ADDRESS);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao buscar parâmetro P_PATRIMONIO_SERVICE_ADDRESS",e);
			throw new ApplicationBusinessException(e);
		}
		return result;
	}
}
