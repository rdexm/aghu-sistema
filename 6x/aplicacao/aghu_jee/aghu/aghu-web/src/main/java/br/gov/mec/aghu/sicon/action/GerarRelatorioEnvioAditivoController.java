package br.gov.mec.aghu.sicon.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.sicon.vo.EnvioAditivoContratoSiconVO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;


public class GerarRelatorioEnvioAditivoController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(GerarRelatorioEnvioAditivoController.class);

	private static final long serialVersionUID = -5891318506144325993L;

	@EJB
	private static ISiconFacade siconFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;		

	private List<EnvioAditivoContratoSiconVO> colecao = new ArrayList<EnvioAditivoContratoSiconVO>(0);
	
	@Override
	public Collection<EnvioAditivoContratoSiconVO> recuperarColecao() {
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/sicon/report/envioAditivo.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("nomeRelatorio", "ENVIOADITIVO");
		try {
			params.put("logoSusPath", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/sicon/report/");
		return params;
	}

	public void geraRelatorio(DadosEnvioVO dadosEnvioVO, ScoAditContrato aditivoContrato)
			throws BaseException {

		// Geração do relatório
		colecao = retornaAditivoVO(dadosEnvioVO, aditivoContrato);
		DocumentoJasper documento = gerarDocumento();

		// Gravação no banco
		try {
			byte[] byteArray = documento.getPdfByteArray(false);

			// Usado para auxílio em desenvolvimento		
		    //FileUtils.writeByteArrayToFile(new File("/tmp/relatorio.pdf"), byteArray);

			if (dadosEnvioVO.getLogEnvioSicon() != null) {
				dadosEnvioVO.getLogEnvioSicon().setArqRel(byteArray);
				siconFacade.atualizaLogEnvioSicon(dadosEnvioVO
						.getLogEnvioSicon());
			}

		} catch (Exception e) {
			LOG.error("Erro ao gerar relatorio contratos sicon", e);
			apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}

	public List<EnvioAditivoContratoSiconVO> retornaAditivoVO(
			DadosEnvioVO dadosEnvioVO, ScoAditContrato aditivoContrato) throws BaseException {
						
		List<EnvioAditivoContratoSiconVO> listaEnvioAditivoSiconVO = new ArrayList<EnvioAditivoContratoSiconVO>();
		EnvioAditivoContratoSiconVO envioAditivoSiconVO = new EnvioAditivoContratoSiconVO();
		
		ScoContrato contrato = aditivoContrato.getCont();
		
		String numContrato = contrato.getNrContrato().toString();	
		envioAditivoSiconVO.setNumeroContrato(NumberUtils.createInteger(numContrato));
		
		if (contrato.getFornecedor() != null) {
			envioAditivoSiconVO.setFornecedor(contrato.getFornecedor()
					.getCpfCnpj()
					+ " - "
					+ contrato.getFornecedor().getRazaoSocial());
		}
		
		envioAditivoSiconVO.setValorTotal(contrato.getValorTotal());
		
		if (contrato.getLicitacao() != null) {
			envioAditivoSiconVO.setProcesso(contrato.getLicitacao().getNumero().toString()); 
		}
		
		envioAditivoSiconVO.setNumeroAditivo(aditivoContrato.getId().getSeq());
		
		envioAditivoSiconVO.setTipoContrato(contrato.getTipoContratoSicon().getDescricao());
		
		envioAditivoSiconVO.setDtInicioVigencia(aditivoContrato.getDtInicioVigencia());
		
		envioAditivoSiconVO.setDtFimVigencia(aditivoContrato.getDtFimVigencia());
		
		envioAditivoSiconVO.setDataRescisao(aditivoContrato.getDataRescicao());
		
		envioAditivoSiconVO.setTipoAditivo(aditivoContrato.getTipoContratoSicon().getDescricao());
		
		envioAditivoSiconVO.setValorAditivado(aditivoContrato.getVlAditivado());
		
		envioAditivoSiconVO.setObjeto(aditivoContrato.getObjetoContrato());
		
		envioAditivoSiconVO.setJustificativa(aditivoContrato.getJustificativa());
		
		envioAditivoSiconVO.setDtAssinatura(aditivoContrato.getDataAssinatura());
		
		envioAditivoSiconVO.setDtPublicacao(aditivoContrato.getDataPublicacao());

		// Rodapé
		envioAditivoSiconVO.setDtEnvio(new java.util.Date());
		envioAditivoSiconVO.setAcao(dadosEnvioVO.getCnet().getAcao());
		envioAditivoSiconVO.setUsuarioResponsavel(registroColaboradorFacade
				.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado()).getPessoaFisica().getNome());

		listaEnvioAditivoSiconVO.add(envioAditivoSiconVO);
		return listaEnvioAditivoSiconVO;
	}

}
