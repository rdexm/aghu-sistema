package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class AdicionarEquipamentoEmLoteController extends ActionController {

	private static final String MANTER_ATIVIDADES = "manterAtividades";
	private static final long serialVersionUID = 6649612877148582990L;

	@EJB
	private ICustosSigFacade custosSigFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	@Inject
	private ManterEquipamentosAtividadeController equipamentosController;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private SigAtividades atividade;
	private FccCentroCustos centroCusto;
	private Integer codigoEquipamento;
	private SigDirecionadores sigDirecionadores;

	private String nomeEquipamento;
	private List<SigDirecionadores> listaDirecionadores;
	private List<SigAtividadeEquipamentos> listEquipamentoAtividade;
	private List<SigAtividadeEquipamentos> listEquipamentoAtividadeAdicionadoLote;

	private String descricaoBem;
	private BigDecimal valorBem;
	private BigDecimal valorDepreciado;
	private boolean integracaoPatrimonioOnline = true;
	private boolean marcarLote;

	private boolean ativo = false;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.nomeEquipamento = "";
		this.codigoEquipamento = null;
		this.sigDirecionadores = null;
		this.listEquipamentoAtividade = new ArrayList<SigAtividadeEquipamentos>();
		this.listEquipamentoAtividadeAdicionadoLote = null;
		this.marcarLote = false;
		this.ativo = false;
	
	}

	public void pesquisar() throws ApplicationBusinessException {
		this.setAtivo(true);
		this.marcarLote = false;
		if ((this.nomeEquipamento == null || this.nomeEquipamento.equals("")) && (this.codigoEquipamento == null)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PESQUISA_OBRIGATORIO_NOME_OU_CODIGO");
			return;
		}
		
		Integer codigoCC = this.getCodigoCentroCusto();

		boolean isPesquisaById = (this.nomeEquipamento.equals(""));
		this.listEquipamentoAtividade = new ArrayList<SigAtividadeEquipamentos>();
		if (isPesquisaById) {
			EquipamentoSistemaPatrimonioVO equipamentoSistemaPatrimonioVO = this.custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(
					this.codigoEquipamento.toString(), codigoCC);
			if (equipamentoSistemaPatrimonioVO != null && equipamentoSistemaPatrimonioVO.getCodigo() != null) {
				SigAtividadeEquipamentos equipamento = new SigAtividadeEquipamentos();
				equipamento.setIndSituacao(DominioSituacao.A);
				equipamento.setCodPatrimonio(equipamentoSistemaPatrimonioVO.getCodigo());
				equipamento.setCodigoCC(codigoCC);
				this.listEquipamentoAtividade.add(equipamento);
			}
		} else {
			List<EquipamentoSistemaPatrimonioVO> retorno = this.custosSigFacade
					.pesquisarEquipamentoSistemaPatrimonioByDescricao(this.nomeEquipamento, codigoCC);
			this.ordenaResultado(retorno);
			for (EquipamentoSistemaPatrimonioVO equipamentoSistemaPatrimonioVO : retorno) {
				SigAtividadeEquipamentos equipamento = new SigAtividadeEquipamentos();
				equipamento.setIndSituacao(DominioSituacao.A);
				equipamento.setCodPatrimonio(equipamentoSistemaPatrimonioVO.getCodigo());
				equipamento.setCodigoCC(codigoCC);
				this.listEquipamentoAtividade.add(equipamento);
			}
		}

		if (!this.nomeEquipamento.equals("") && codigoEquipamento != null && !codigoEquipamento.equals("")) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PESQUISA_FEITA_POR_NOME");
			return;
		}
	}

	private void ordenaResultado(List<EquipamentoSistemaPatrimonioVO> lista) {
		Collections.sort(lista, new Comparator<EquipamentoSistemaPatrimonioVO>() {
			@Override
			public int compare(EquipamentoSistemaPatrimonioVO o1, EquipamentoSistemaPatrimonioVO o2) {
				return o1.getDescricao().compareTo(o2.getDescricao());
			}
		});

	}

	public List<SigDirecionadores> getListaDirecionadores() {
		listaDirecionadores = new ArrayList<SigDirecionadores>();
		listaDirecionadores = custosSigCadastrosBasicosFacade.pesquisarDirecionadoresAtivosInativo(Boolean.TRUE, Boolean.TRUE);
		return listaDirecionadores;
	}

	public String getDescricaoBem(SigAtividadeEquipamentos equipamento) {
		EquipamentoSistemaPatrimonioVO equipamentoVo = buscaEquipamentoModuloPatrimonio(equipamento);
		if (equipamentoVo == null) {
			this.setDescricaoBem(null);
			this.setValorBem(null);
			this.setValorDepreciado(null);
			return descricaoBem;
		} else {
			this.setDescricaoBem(equipamentoVo.getDescricao());
			this.setValorBem(equipamentoVo.getValor());
			this.setValorDepreciado(equipamentoVo.getValorDepreciado());
			return descricaoBem;
		}
	}

	public String cancelar() {
		equipamentosController.getManterAtividadesController().setAdicionadoEquipamentoLote(true);
		return MANTER_ATIVIDADES;
	}

	public String adicionar() throws ApplicationBusinessException {

		if (sigDirecionadores == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_DIRECIONADOR_OBRIGATORIO");
			return null;
		}

		listEquipamentoAtividadeAdicionadoLote = new ArrayList<SigAtividadeEquipamentos>();
		for (SigAtividadeEquipamentos equipamento : listEquipamentoAtividade) {
			if (equipamento.getSelected()) {
				equipamento.setCodigoCC(this.getCodigoCentroCusto());
				equipamento.setSigDirecionadores(sigDirecionadores);
				equipamento.setCriadoEm(new Date());
				equipamento.setServidorResp(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				equipamento.setSigAtividades(equipamentosController.getManterAtividadesController().getAtividade());
				listEquipamentoAtividadeAdicionadoLote.add(equipamento);
			}

		}
		if (listEquipamentoAtividadeAdicionadoLote.isEmpty() || listEquipamentoAtividadeAdicionadoLote.size() == 0) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_INCLUSAO_LOTE_SEM_ITEM");
			return null;
		}
		equipamentosController.adicionaEquipamentosEmLote(listEquipamentoAtividadeAdicionadoLote);
		equipamentosController.getManterAtividadesController().setAdicionadoEquipamentoLote(true);
		equipamentosController.setPossuiAlteracao(true);
		return MANTER_ATIVIDADES;
	}

	public void selectedAdicionarEquipamentoAtividade(SigAtividadeEquipamentos equipamento) {
		for (SigAtividadeEquipamentos equipamentoIt : listEquipamentoAtividade) {
			if (equipamentoIt.getCodPatrimonio().equalsIgnoreCase(equipamento.getCodPatrimonio())) {
				equipamentoIt.setSelected(equipamento.getSelected());
				return;
			}
		}
	}

	public void selecionaLote() {
		this.atualizaCheckBoxListLote(this.getMarcarLote());
	}

	private void atualizaCheckBoxListLote(boolean marcar) {
		for (SigAtividadeEquipamentos equipamentoIt : listEquipamentoAtividade) {
			equipamentoIt.setSelected(marcar);
		}
	}

	private EquipamentoSistemaPatrimonioVO buscaEquipamentoModuloPatrimonio(SigAtividadeEquipamentos equipamento) {
		try {
			Integer codigoCC = this.getCodigoCentroCusto();
			if (equipamento.getCodigoCC() != null && codigoCC != null && equipamento.getCodigoCC().intValue() != codigoCC.intValue()) {
				codigoCC = equipamento.getCodigoCC();
			}
			return custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(equipamento.getCodPatrimonio(), codigoCC);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_FALHA");
			return null;
		}
	}

	private Integer getCodigoCentroCusto(){
		if(this.getCentroCusto() == null){
			return 0;
		}
		else{
			return this.getCentroCusto().getCodigo();
		}
	}
	
	// Getters and Setters

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public SigAtividades getAtividade() {
		return atividade;
	}

	public void setAtividade(SigAtividades atividade) {
		this.atividade = atividade;
	}

	public Integer getCodigoEquipamento() {
		return codigoEquipamento;
	}

	public void setCodigoEquipamento(Integer codigoEquipamento) {
		this.codigoEquipamento = codigoEquipamento;
	}

	public String getNomeEquipamento() {
		return nomeEquipamento;
	}

	public void setNomeEquipamento(String nomeEquipamento) {
		this.nomeEquipamento = nomeEquipamento;
	}

	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	public List<SigAtividadeEquipamentos> getListEquipamentoAtividade() {
		return listEquipamentoAtividade;
	}

	public void setListEquipamentoAtividade(List<SigAtividadeEquipamentos> listEquipamentoAtividade) {
		this.listEquipamentoAtividade = listEquipamentoAtividade;
	}

	public BigDecimal getValorBem() {
		return valorBem;
	}

	public void setValorBem(BigDecimal valorBem) {
		this.valorBem = valorBem;
	}

	public BigDecimal getValorDepreciado() {
		return valorDepreciado;
	}

	public void setValorDepreciado(BigDecimal valorDepreciado) {
		this.valorDepreciado = valorDepreciado;
	}

	public String getDescricaoBem() {
		return descricaoBem;
	}

	public void setDescricaoBem(String descricaoBem) {
		this.descricaoBem = descricaoBem;
	}

	public boolean isIntegracaoPatrimonioOnline() {
		return integracaoPatrimonioOnline;
	}

	public void setIntegracaoPatrimonioOnline(boolean integracaoPatrimonioOnline) {
		this.integracaoPatrimonioOnline = integracaoPatrimonioOnline;
	}

	public List<SigAtividadeEquipamentos> getListEquipamentoAtividadeAdicionadoLote() {
		return listEquipamentoAtividadeAdicionadoLote;
	}

	public void setListEquipamentoAtividadeAdicionadoLote(List<SigAtividadeEquipamentos> listEquipamentoAtividadeAdicionadoLote) {
		this.listEquipamentoAtividadeAdicionadoLote = listEquipamentoAtividadeAdicionadoLote;
	}

	public Boolean getMarcarLote() {
		return marcarLote;
	}

	public void setMarcarLote(Boolean marcarLote) {
		this.marcarLote = marcarLote;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}
