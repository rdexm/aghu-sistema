package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.model.MpmNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.CabecalhoAnamneseEvolucaoController;
import br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action.RelatorioEvolucoesPacienteController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.MpmEvolucoesVO;


public class VisualizarEvolucoesPOLController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(VisualizarEvolucoesPOLController.class);

	private static final long serialVersionUID = 2175770723653572327L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private CabecalhoAnamneseEvolucaoController cabecalhoAnamneseEvolucaoController;

	@Inject
	private RelatorioEvolucoesPacienteController relatorioEvolucoesPacienteController;

	private List<MpmEvolucoes> listaEvolucoes;
	private MpmEvolucoes evolucao;
	
	private String dataReferencia;
	private List<String> listaDataReferencia;
	private List<MpmEvolucoesVO> evolucoesVO;
	private List<RapServidores> listaProfissionais;
	private List<MpmEvolucoes> listaEvolucoesSelecionadas;
	private RapServidores profissional;

	private Long seqAnamnese;
	private Integer seqAtendimento;
	private String voltarPara;
	private String cameFrom;

	private Boolean allChecked;

	public String iniciar() {
		if (this.seqAnamnese != null && this.seqAtendimento != null && !"relatorioEvolucoesPaciente".equals(this.cameFrom)) {
			iniciarCabecalho();
			MpmAnamneses anamnese = this.prescricaoMedicaFacade.obterAnamneseValidadaPorAnamneses(this.seqAnamnese);
			if (anamnese == null) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_ANAMNESE_EXCLUIDA");
				return this.voltarPara;
			}
			this.listaEvolucoes = this.prescricaoMedicaFacade.listarEvolucoesConcluidasAnamnese(seqAnamnese);
			if (this.listaEvolucoes == null || this.listaEvolucoes.isEmpty()) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_EVOLUCOES_EXCLUIDAS");
				return this.voltarPara;
			}

			carregarListaDataReferencia();
			carregarListaEvolucoesVO();
			carregarProfissionais();
			atualizarEvolucoesSelecionadas();

			this.evolucao = this.listaEvolucoes.get(0);
		}
		return null;
	}

	private void carregarListaEvolucoesVO() {
		this.evolucoesVO = new ArrayList<MpmEvolucoesVO>();

		for (MpmEvolucoes evolucao : listaEvolucoes) {
			MpmEvolucoesVO evolucaoVO = new MpmEvolucoesVO();
			evolucaoVO.setEvolucao(evolucao);
			this.evolucoesVO.add(evolucaoVO);
		}
	}

	private void carregarListaDataReferencia() {

		this.listaDataReferencia = new ArrayList<String>();

		if (listaEvolucoes != null && !listaEvolucoes.isEmpty()) {
			for (MpmEvolucoes evolucao : listaEvolucoes) {
				String dataFormatada = DateFormatUtil.fomataDiaMesAno(evolucao.getDthrReferencia());
				if (!this.listaDataReferencia.contains(dataFormatada)) {
					this.listaDataReferencia.add(DateFormatUtil.fomataDiaMesAno(evolucao.getDthrReferencia()));
				}
			}
			this.dataReferencia = listaDataReferencia.get(0);
		}
	}

	private void carregarProfissionais() {

		Set<RapServidores> profissionais = new HashSet<RapServidores>();

		for (MpmEvolucoes evolucao : listaEvolucoes) {
			profissionais.add(evolucao.getServidor());
		}

		this.listaProfissionais = new ArrayList<RapServidores>(profissionais);
	}

	public void filtrarEvolucoes() {

		Date referencia = obterDataReferencia();

		this.evolucoesVO = new ArrayList<MpmEvolucoesVO>();

		for (MpmEvolucoes evolucao : listaEvolucoes) {
			if (DateUtil.isDatasIguais(DateUtil.truncaData(referencia), DateUtil.truncaData(evolucao.getDthrReferencia()))) {
				MpmEvolucoesVO evolucaoVO = new MpmEvolucoesVO();
				evolucaoVO.setEvolucao(evolucao);
				if (profissional == null) {
					this.evolucoesVO.add(evolucaoVO);
				} else if (profissional.equals(evolucao.getServidor())) {
					this.evolucoesVO.add(evolucaoVO);
				}
			}
		}

		if (evolucoesVO.isEmpty()) {
			this.evolucao = null;
		} else {
			this.evolucao = this.evolucoesVO.get(0).getEvolucao();
		}

		listarNotasAdicionais();
		this.allChecked = Boolean.FALSE;
	}

	private void iniciarCabecalho() {
		this.cabecalhoAnamneseEvolucaoController.setSeqAtendimento(this.seqAtendimento);
		this.cabecalhoAnamneseEvolucaoController.iniciar();
	}

	public List<MpmNotaAdicionalEvolucoes> listarNotasAdicionais() {
		if (this.evolucao != null) {
			return this.prescricaoMedicaFacade.listarNotasAdicionaisEvolucao(this.evolucao.getSeq());
		}
		return null;
	}

	private Date obterDataReferencia() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return new Date(format.parse(this.dataReferencia).getTime());
		} catch (ParseException e) {
			LOG.error("Erro ao convereter data.");
		}
		return null;
	}

	public void checkAll() {
		for (MpmEvolucoesVO evolucaoVO : evolucoesVO) {
			if (this.allChecked) {
				evolucaoVO.setSelecionado(Boolean.TRUE);
			} else {
				evolucaoVO.setSelecionado(Boolean.FALSE);
			}
		}

		atualizarEvolucoesSelecionadas();
	}

	public String getMensagemTotalEvolucoesSelecionadas() {

		int total = 0;
		if (evolucoesVO != null && !evolucoesVO.isEmpty()) {
			for (MpmEvolucoesVO evolucaoVO : evolucoesVO) {
				if (evolucaoVO != null && Boolean.TRUE.equals(evolucaoVO.getSelecionado())) {
					total++;
				}
			}
		}

		return MessageFormat.format(this.getBundle().getString("MENSAGEM_ANAMNESE_EVOLUCAO_IMPRIMIR_EVOLUCOES"), total);
	}

	public void imprimirEvolucoes() {
		for (MpmEvolucoesVO evolucaoVO : evolucoesVO) {
			if (evolucaoVO != null && Boolean.TRUE.equals(evolucaoVO.getSelecionado())) {
				this.relatorioEvolucoesPacienteController.imprimirEvolucao(evolucaoVO.getEvolucao().getSeq());
			}
		}
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
	}

	public void atualizarEvolucoesSelecionadas() {
		this.listaEvolucoesSelecionadas = new ArrayList<MpmEvolucoes>();
		for (MpmEvolucoesVO evolucaoVO : evolucoesVO) {
			if (evolucaoVO != null && Boolean.TRUE.equals(evolucaoVO.getSelecionado())) {
				this.listaEvolucoesSelecionadas.add(evolucaoVO.getEvolucao());
			}
		}
	}

	public String visualizarImpressaoEvolucoes() {
		this.relatorioEvolucoesPacienteController.visualizarEvolucoes(listaEvolucoesSelecionadas);
		return "visualizarImpressaoEvolucoes";
	}

	public List<MpmEvolucoes> getListaEvolucoes() {
		return listaEvolucoes;
	}

	public MpmEvolucoes getEvolucao() {
		return evolucao;
	}

	public void setEvolucao(MpmEvolucoes evolucao) {
		this.evolucao = evolucao;
		if (this.evolucao != null) {
			this.dataReferencia = DateFormatUtil.fomataDiaMesAno(this.evolucao.getDthrReferencia());
		}
	}

	public String getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(String dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

	public List<String> getListaDataReferencia() {
		return listaDataReferencia;
	}

	public Long getSeqAnamnese() {
		return seqAnamnese;
	}

	public void setSeqAnamnese(Long seqAnamnese) {
		this.seqAnamnese = seqAnamnese;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public List<MpmEvolucoesVO> getEvolucoesVO() {
		return evolucoesVO;
	}

	public void setEvolucoesVO(List<MpmEvolucoesVO> evolucoesVO) {
		this.evolucoesVO = evolucoesVO;
	}

	public Boolean getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(Boolean allChecked) {
		this.allChecked = allChecked;
	}

	public List<RapServidores> getListaProfissionais() {
		return listaProfissionais;
	}

	public void setListaProfissionais(List<RapServidores> listaProfissionais) {
		this.listaProfissionais = listaProfissionais;
	}

	public RapServidores getProfissional() {
		return profissional;
	}

	public void setProfissional(RapServidores profissional) {
		this.profissional = profissional;
	}

	public List<MpmEvolucoes> getListaEvolucoesSelecionadas() {
		return listaEvolucoesSelecionadas;
	}

	public void setListaEvolucoesSelecionadas(List<MpmEvolucoes> listaEvolucoesSelecionadas) {
		this.listaEvolucoesSelecionadas = listaEvolucoesSelecionadas;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

}