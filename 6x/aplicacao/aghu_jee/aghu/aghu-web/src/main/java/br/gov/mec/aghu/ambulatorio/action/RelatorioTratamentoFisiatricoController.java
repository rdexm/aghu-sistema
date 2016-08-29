package br.gov.mec.aghu.ambulatorio.action;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.security.Base64Utils;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioTratamentoFisiatricoVO;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.vo.DescricaoCodigoComplementoCidVO;
import br.gov.mec.aghu.paciente.vo.QtdSessoesTratamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImagemModalidadeOrientacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

//import com.sun.org.apache.xml.internal.security.utils.Base64;

public class RelatorioTratamentoFisiatricoController extends ActionReport {

	private static final long serialVersionUID = 385071052161723432L;

	private RelatorioTratamentoFisiatricoVO relatorioTratamentoFisiatricoVO;
	
	private String descricaoDocumento;
	
	private List<RelatorioTratamentoFisiatricoVO> colecao;
	
	private StreamedContent media;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private final String ESPACO = " "; 
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void directPrint() {
		
		try {			
			colecao = recuperarColecao();			
//			DocumentoJasper documento = gerarDocumento();
//			this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
//			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", descricaoDocumento);	
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
		try {
			DocumentoJasper documento = gerarDocumento();
			
			sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", descricaoDocumento);
		
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
	}
	
	@Override
	protected List<RelatorioTratamentoFisiatricoVO> recuperarColecao()
			throws ApplicationBusinessException {
		List<RelatorioTratamentoFisiatricoVO> relatorio = new ArrayList<RelatorioTratamentoFisiatricoVO>();
		
		List<ImagemModalidadeOrientacaoVO> listaImagemModalidadeOrientacao = relatorioTratamentoFisiatricoVO.getListaImagemModalidadeOrientacaoVO();
		if (listaImagemModalidadeOrientacao != null && !listaImagemModalidadeOrientacao.isEmpty()) {
			int i = 1;
			for (ImagemModalidadeOrientacaoVO vo : listaImagemModalidadeOrientacao) {
				vo.setContador(i+".");
				transformarImagem(vo);
				formatarModalidade(vo);
				formatarOrientacao(vo);
				i++;
			}
		}		
		
		List<DescricaoCodigoComplementoCidVO> listaDescricaoCodigoComplementoCidVO = relatorioTratamentoFisiatricoVO.getListaDescricaoCodigoComplementoCidVO();		
		for (DescricaoCodigoComplementoCidVO vo: listaDescricaoCodigoComplementoCidVO) {
			formatarCidDescricao(vo);
		}
		
		QtdSessoesTratamentoVO qtdSessoesTratamentoVO = relatorioTratamentoFisiatricoVO.getQtdSessoesTratamentoVO();
		formatarTratamento(qtdSessoesTratamentoVO);
		formatarQtdeSessoesTratamento(qtdSessoesTratamentoVO);
		
		MptPrescricaoPaciente prescricaoPaciente = relatorioTratamentoFisiatricoVO.getPrescricaoPaciente();
		if (prescricaoPaciente != null && prescricaoPaciente.getId() != null) {
			AghAtendimentos aghAtendimentos = ambulatorioFacade.obterAtendimentoPorSeq(prescricaoPaciente.getId().getAtdSeq());
			String localizacaoPaciente = farmaciaFacade.obterLocalizacaoPacienteParaRelatorio(aghAtendimentos);
			relatorioTratamentoFisiatricoVO.setLocalizacaoPaciente(localizacaoPaciente);
			
			MptPrescricaoPaciente prescricaoPacienteServidor = ambulatorioFacade.obterVinculoMatriculaResponsavel(prescricaoPaciente.getId().getAtdSeq(), prescricaoPaciente.getId().getSeq());
			if (prescricaoPacienteServidor != null) {
				String responsavel = prontuarioOnlineFacade.formataNomeProf(prescricaoPacienteServidor.getSerMatriculaValida(), prescricaoPacienteServidor.getSerVinCodigoValida());
				relatorioTratamentoFisiatricoVO.setResponsavelFormatado(responsavel);
			}
		}
		
		AipPacientes aipPacientes = relatorioTratamentoFisiatricoVO.getAipPacientes();
		String prontuarioFormatado = formatarProntuario(aipPacientes);
		relatorioTratamentoFisiatricoVO.setProntuarioFormatado(prontuarioFormatado);
		
		relatorio.add(relatorioTratamentoFisiatricoVO);
		return relatorio;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioTratamentoFisiatrico.jasper";
	}
	
	public Map<String, Object> recuperarParametros() {
		
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("caminhoLogo", FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images/logoClinicas.jpg"));
		
		return params;
	}
	
	private void transformarImagem(ImagemModalidadeOrientacaoVO vo) {
		String imagemString = "";
		
		if (vo.getImagem() != null) {
			imagemString = Base64Utils.tob64(vo.getImagem());
			byte[] bytesConvertidos = Base64Utils.fromb64(imagemString);
			vo.setImagemInputStream(new ByteArrayInputStream(bytesConvertidos));								
		}
	}
	
	private void formatarModalidade(ImagemModalidadeOrientacaoVO vo) {
		
		String descricaoTipoModalidade = ambulatorioFacade.obterDescricaoCidCapitalizada(vo.getDescricaoTipoModalidade());		
		String modalidadeFormatada = descricaoTipoModalidade.concat(ESPACO).concat(vo.getNumVezesSemana().toString()).concat(" x por semana");		
		vo.setModalidadeFormatada(modalidadeFormatada);		
	}
	
	private void formatarCidDescricao(DescricaoCodigoComplementoCidVO vo) {
		
		String cidFormatado = "";
		
		if (vo != null && vo.getCidCodigo() != null) {
			String descricaoCid = ambulatorioFacade.obterDescricaoCidCapitalizada(vo.getCidDescricao());
			cidFormatado = descricaoCid.concat(ESPACO).concat(vo.getCidCodigo());
			if (vo.getComplementoCidTratTerapeutico() != null) {
				cidFormatado = cidFormatado.concat(ESPACO).concat(vo.getComplementoCidTratTerapeutico());
			}
		}
		
		vo.setCidDescricaoFormatado(cidFormatado);
	}
	
	private void formatarOrientacao(ImagemModalidadeOrientacaoVO vo) {
		
		String orientacaoFormatada = ambulatorioFacade.obterDescricaoCidCapitalizada(vo.getOrientacoesItemPrcrModalidade());				
		vo.setOrientacaoFormatada(orientacaoFormatada);	
	}
	
	private void formatarTratamento(QtdSessoesTratamentoVO vo) {
		String tratamentoFormatado = "";
		if (vo != null) {
			if (vo.getDescricaoProcedHospInterno() == null) {
				tratamentoFormatado = "APAC";
			} else {
				tratamentoFormatado = "Código "
						.concat(vo.getSeqProcedHospInterno().toString())
						.concat(" - ")
						.concat(ambulatorioFacade
								.obterDescricaoCidCapitalizada(vo
										.getDescricaoProcedHospInterno()));
			}
		}
		vo.setTratamentoFormatado(tratamentoFormatado);
	}
	
	private void formatarQtdeSessoesTratamento(QtdSessoesTratamentoVO vo) {
		String qtdeSessoesFormatada = "0";
		if (vo != null) {
			if (vo.getIndSessoesContinuas() != null && vo.getIndSessoesContinuas()) {
				qtdeSessoesFormatada = "Contínuas";			
			} else if (vo.getIndSessoesContinuas() != null && vo.getQtdSessoes() != null) {
				qtdeSessoesFormatada = vo.getQtdSessoes().toString();
			}
		}
		vo.setQtdSessoesTratamentoFormatada(qtdeSessoesFormatada);
	}
	
	private String formatarProntuario(AipPacientes paciente) {
		String prontuarioFormatado = "";
		if (paciente.getProntuario() != null) {
			int tamanhoString = paciente.getProntuario().toString().length();			
			prontuarioFormatado = paciente.getProntuario().toString().substring(0, tamanhoString-1).concat("/").concat(paciente.getProntuario().toString().substring(tamanhoString-1, tamanhoString)); 
		}
		
		return prontuarioFormatado;
	}
	
	public RelatorioTratamentoFisiatricoVO getRelatorioTratamentoFisiatricoVO() {
		return relatorioTratamentoFisiatricoVO;
	}

	public void setRelatorioTratamentoFisiatricoVO(
			RelatorioTratamentoFisiatricoVO relatorioTratamentoFisiatricoVO) {
		this.relatorioTratamentoFisiatricoVO = relatorioTratamentoFisiatricoVO;
	}

	public String getDescricaoDocumento() {
		return descricaoDocumento;
	}

	public void setDescricaoDocumento(String descricaoDocumento) {
		this.descricaoDocumento = descricaoDocumento;
	}	

	public List<RelatorioTratamentoFisiatricoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioTratamentoFisiatricoVO> colecao) {
		this.colecao = colecao;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

}
