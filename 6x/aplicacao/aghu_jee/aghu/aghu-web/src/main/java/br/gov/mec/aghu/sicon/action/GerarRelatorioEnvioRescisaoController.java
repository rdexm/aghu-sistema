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
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.sicon.vo.EnvioRescisaoContratoSiconVO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;


public class GerarRelatorioEnvioRescisaoController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(GerarRelatorioEnvioRescisaoController.class);
	
	private static final long serialVersionUID = -8688931583632372686L;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private static ISiconFacade siconFacade;

	private List<EnvioRescisaoContratoSiconVO> colecao = new ArrayList<EnvioRescisaoContratoSiconVO>(0);
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	@Override
	public Collection<EnvioRescisaoContratoSiconVO> recuperarColecao() {
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/sicon/report/envioRescisao.jasper";
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("nomeRelatorio", "ENVIORESCISAO");
		try {
			params.put("logoSusPath", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}			
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/sicon/report/");
		return params;
	}	
	
	public void geraRelatorio(DadosEnvioVO dadosEnvioVO)
			throws BaseException {
		
		// Geração do relatório
		colecao = retornaRescisaoVO(dadosEnvioVO);
		DocumentoJasper documento = gerarDocumento();

		// Gravação no banco
		try {
			byte[] byteArray = documento.getPdfByteArray(false);
			
			// Usado para auxílio em desenvolvimento
			// FileUtils.writeByteArrayToFile(new File("/tmp/relatorio.pdf"), byteArray);

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
	
	public List<EnvioRescisaoContratoSiconVO> retornaRescisaoVO(
			DadosEnvioVO dadosEnvioVO) throws BaseException {
		
		List<EnvioRescisaoContratoSiconVO> listaEnvioRescisaoContratoSiconVO = new ArrayList<EnvioRescisaoContratoSiconVO>();

		EnvioRescisaoContratoSiconVO envioRescisaoContratoSiconVO = new EnvioRescisaoContratoSiconVO();

		
		ScoContrato contrato = siconFacade
				.obterContratoPorNumeroContrato(Long.valueOf(dadosEnvioVO.getCnet()
						.getNumero()));
		String numContrato = contrato.getNrContrato().toString();
		envioRescisaoContratoSiconVO.setNumeroContrato(NumberUtils.createInteger(numContrato));

		if (contrato.getFornecedor() != null) {
			envioRescisaoContratoSiconVO.setFornecedor(contrato.getFornecedor()
					.getCpfCnpj()
					+ " - "
					+ contrato.getFornecedor().getRazaoSocial());
		}
		
		envioRescisaoContratoSiconVO.setValorTotal(contrato.getValorTotal());

		if (contrato.getLicitacao() != null) {
			envioRescisaoContratoSiconVO.setProcesso(contrato.getLicitacao().getNumero().toString()); 
		}

		if (contrato.getRescicao().getIndTipoRescisao() != null){
			envioRescisaoContratoSiconVO.setTipoRescisao(contrato.getRescicao().getIndTipoRescisao().getDescricao());
		}
		
		if (contrato.getRescicao() != null){
			envioRescisaoContratoSiconVO.setJustificativa(contrato.getRescicao().getJustificativa());
		}
		
		envioRescisaoContratoSiconVO.setDtAssinatura(contrato.getDtAssinatura());

		envioRescisaoContratoSiconVO.setDtPublicacao(contrato.getDtPublicacao());
		
		envioRescisaoContratoSiconVO.setDtEnvio(new java.util.Date());

		envioRescisaoContratoSiconVO.setAcao(dadosEnvioVO.getCnet().getAcao());

		envioRescisaoContratoSiconVO.setUsuarioResponsavel(registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado()).getPessoaFisica().getNome());
		
		listaEnvioRescisaoContratoSiconVO.add(envioRescisaoContratoSiconVO);
		
		return listaEnvioRescisaoContratoSiconVO;

	}	
	
}
