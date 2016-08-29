package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioPeriodicidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.FatConvPlanoAcomodacoes;
import br.gov.mec.aghu.model.FatConvPlanoAcomodacoesId;
import br.gov.mec.aghu.model.FatConvTipoDocumentos;
import br.gov.mec.aghu.model.FatConvTipoDocumentosId;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatPeriodosEmissao;
import br.gov.mec.aghu.model.FatPeriodosEmissaoId;
import br.gov.mec.aghu.model.FatTiposDocumento;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ConvenioSaudeController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -720541755963412961L;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	/**
	 * Código do Convênio a ser editado/criado..
	 */
	private Short codigo;

	private FatConvenioSaude fatConvenioSaude;
	private Boolean csAtivo = true;
	private Boolean csPlanoAtivo = true;
	private AacPagador aacPagador;
	private AipUfs aipUf;
	private Short seqTipoDoc;
	private String seqDescTipoDocLov;
	private FatTiposDocumento tipoDocumento;
	private List<FatTiposDocumento> tiposDocumentos = new ArrayList<FatTiposDocumento>();
	private Integer seqAcomodacao;
	private String seqDescAcomodacao;
	private AinAcomodacoes acomodacao;
	private List<AinAcomodacoes> acomodacoes = new ArrayList<AinAcomodacoes>();
	private FatConvenioSaudePlano convenioSaudePlano;
	private List<FatConvenioSaudePlano> convenioSaudePlanos = new ArrayList<FatConvenioSaudePlano>(0);

	/**
	 * Variável que controla fechamento do modal.
	 */
	private boolean operacaoConcluida = false;
	private Integer diaInicio;
	private Integer diaFim;
	private Integer diaSemana;
	private DominioPeriodicidade periodicidade;
	private DominioSituacao indSituacao;
	private List<FatPeriodosEmissao> convPeriodos = new ArrayList<FatPeriodosEmissao>(0);
	private List<FatConvTipoDocumentos> convTipoDocumentos = new ArrayList<FatConvTipoDocumentos>(0);
	private List<FatConvPlanoAcomodacoes> convPlanoAcomodacoes = new ArrayList<FatConvPlanoAcomodacoes>(0);

	private final String PAGE_PESQUISAR_CONVENIO_SAUDE = "convenioSaudeList";
	private final String PAGE_CONVENIO_SAUDE_CRUD = "convenioSaudeCRUD";
	private final String PAGE_PLANOS_LIST = "convenioSaudePlanos";
	private final String PAGE_PLANOS_CRUD = "convenioSaudePlanosCRUD";

    @PostConstruct
    public void inicializar() {
        begin(conversation);
    }
	
	public void init() {
		this.fatConvenioSaude = new FatConvenioSaude();
		this.convenioSaudePlano = new FatConvenioSaudePlano();
		this.tipoDocumento = new FatTiposDocumento();
		this.acomodacao = new AinAcomodacoes();
		this.aacPagador = null;
		this.aipUf = null;

		this.diaInicio = null;
		this.diaFim = null;
		this.diaSemana = null;

		this.seqTipoDoc = null;
		this.seqDescTipoDocLov = null;

		this.seqAcomodacao = null;
		this.seqDescAcomodacao = null;
	}

	public List<FatTiposDocumento> pesquisarTipoDocumento(String param){
		this.tiposDocumentos = this.cadastrosBasicosInternacaoFacade.obterTiposDocs(param);
		return this.tiposDocumentos;
	}
	
	public List<AinAcomodacoes> pesquisarAcomodacoes(String param){
		this.acomodacoes = this.cadastrosBasicosInternacaoFacade.pesquisarAcomodacoesPorCodigoOuDescricaoOrdenado(param);
		return this.acomodacoes;
	}

	public void iniciarInclusao() {
		this.init();

		this.fatConvenioSaude.setRestringeProf(true);

		this.csAtivo = true;
		this.codigo = null;
	}

	/**
	 * Método que define se a operação é Novo/Edição.
	 */
	public void inicio() {
		if (this.codigo != null) {
			this.fatConvenioSaude = this.faturamentoApoioFacade
					.obterConvenioSaudeComPagadorEUF(this.codigo);

			if (this.fatConvenioSaude.getUf() != null) {
				this.aipUf = this.fatConvenioSaude.getUf();
			}

			if (this.fatConvenioSaude.getPagador() != null) {
				this.aacPagador = this.fatConvenioSaude.getPagador();
			}
		}

		if (this.fatConvenioSaude == null) {
			this.fatConvenioSaude = new FatConvenioSaude();
			this.fatConvenioSaude.setRestringeProf(Boolean.TRUE);
			this.fatConvenioSaude.setSituacao(DominioSituacao.A);
		}
	}

	public String confirmar() {
		boolean create = false;

		try {
			this.fatConvenioSaude.setConvenioSaudePlanos(this.faturamentoFacade.pesquisarPlanoPorConvenioSaude(this.fatConvenioSaude.getCodigo()));

			DominioSituacao dominioSituacao = DominioSituacao.A;

			if (!this.csAtivo) {
				dominioSituacao = DominioSituacao.I;
			}

			this.fatConvenioSaude.setSituacao(dominioSituacao);

			// Campo necessário, validará ao salvar.
			this.fatConvenioSaude.setPagador(this.aacPagador);

			// Obtem Grupo Convênio a partir do Pagador.
			this.fatConvenioSaude.setGrupoConvenio(this.faturamentoApoioFacade
					.obterGrupoConvenio(this.aacPagador));

			// Campo não necessário, só associa caso != null.
			if (aipUf != null && StringUtils.isNotBlank(this.aipUf.getSigla())) {
				this.fatConvenioSaude.setUf(this.aipUf);
			} else {
				this.fatConvenioSaude.setUf(null);
			}

			create = this.fatConvenioSaude.getCodigo() == null;

			this.faturamentoApoioFacade.persistir(this.fatConvenioSaude);

			if (create) {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_CONVENIO_SAUDE",this.fatConvenioSaude.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EDICAO_CONVENIO_SAUDE",this.fatConvenioSaude.getDescricao());
			}

			this.init();
		} catch (ApplicationBusinessException e) {
			if (create) {
				this.fatConvenioSaude.setCodigo(null);
			}
			apresentarExcecaoNegocio(e);

			return null;
		}
		// tem que voltar para lá com o botão novo habilitado...
		return PAGE_PESQUISAR_CONVENIO_SAUDE;
	}

	public String cancelar() {
		init();
		return PAGE_PESQUISAR_CONVENIO_SAUDE;
	}

	public String canceladoConvSaudePlanoCrud() {
		this.diaInicio = null;
		this.diaFim = null;
		this.diaSemana = null;

		this.seqTipoDoc = null;
		this.seqDescTipoDocLov = null;

		this.seqAcomodacao = null;
		this.seqDescAcomodacao = null;

		this.tipoDocumento = null;
		this.acomodacao = null;

		return PAGE_PLANOS_LIST;
	}

	/**
	 * Método que verifica se periodos de emissao estão válidos.
	 * 
	 * @return
	 */
	public String verificaPeriodoEmissao() {

		try {
			this.faturamentoApoioFacade.validaPeriodos(this.convenioSaudePlano
					.getConvPeriodos(), this.convenioSaudePlano
					.getPeriodicidadeEmissao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	/**
	 * Método usado para confirmar as operações com Convênio.
	 */
	public String confirmarPlano() {

		// Variável que indica se é a criação ou edição de
		// FatConvenioSaudePlano.
		boolean create = false;

		if (this.convenioSaudePlano.getId() == null) {
			// Novo FatConvenioSaudePlano.
			create = true;
			if (this.fatConvenioSaude.getCodigo() != null) {
				FatConvenioSaudePlanoId id = new FatConvenioSaudePlanoId();
				id.setCnvCodigo(this.fatConvenioSaude.getCodigo());
				this.convenioSaudePlano.setId(id);
			}
		}

		DominioSituacao dominioSituacao = DominioSituacao.A;
		if (!this.csPlanoAtivo) {
			dominioSituacao = DominioSituacao.I;
		}
		this.convenioSaudePlano.setIndSituacao(dominioSituacao);

		this.convenioSaudePlano.setConvenioSaude(this.fatConvenioSaude);

		try {
			// Salvar Plano associados ao Convênio.
			this.convenioSaudePlano = this.faturamentoApoioFacade
					.persistirConvenioPlano(this.fatConvenioSaude,
							this.convenioSaudePlano, this.convPeriodos,
							this.convTipoDocumentos, this.convPlanoAcomodacoes);

		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);

			if (create) {
				this.convenioSaudePlano.setId(null);
			}

			return null;
		}

		if (create) {
			apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_CRIACAO_CONVENIO_SAUDE_PLANO", this.convenioSaudePlano.getDescricao());
		} else {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EDICAO_CONVENIO_SAUDE_PLANO", this.convenioSaudePlano.getDescricao());
		}

		// Associando
		if (!this.convenioSaudePlanos.contains(this.convenioSaudePlano)) {
			this.convenioSaudePlanos.add(this.convenioSaudePlano);
		}

		// Iniciar campos da tela.
		this.diaInicio = null;
		this.diaFim = null;
		this.diaSemana = null;

		this.seqTipoDoc = null;
		this.seqDescTipoDocLov = null;
		this.tipoDocumento = null;

		this.seqAcomodacao = null;
		this.seqDescAcomodacao = null;
		this.acomodacao = null;

		this.convPeriodos = new ArrayList<FatPeriodosEmissao>(0);
		this.convPlanoAcomodacoes = new ArrayList<FatConvPlanoAcomodacoes>(0);
		this.convTipoDocumentos = new ArrayList<FatConvTipoDocumentos>(0);

		return PAGE_CONVENIO_SAUDE_CRUD;
	}

	/**
	 * Método usado para confirmar as operaçães com Convênio.
	 */
	public String confirmarPlanos() {
		return PAGE_CONVENIO_SAUDE_CRUD;
	}

	/**
	 * Método que redireciona para tela de Planos.
	 */
	public String prepararPlanos() {

		if (this.fatConvenioSaude.getCodigo() == null) {
			DominioSituacao dominioSituacao = DominioSituacao.A;

			if (!this.csAtivo) {
				dominioSituacao = DominioSituacao.I;
			}
			this.fatConvenioSaude.setSituacao(dominioSituacao);

			// Campo necessário, validará ao salvar.
			this.fatConvenioSaude.setPagador(this.aacPagador);
			
			// Obtem Grupo Convênio a partir do Pagador.
			this.fatConvenioSaude.setGrupoConvenio(this.faturamentoApoioFacade
					.obterGrupoConvenio(this.aacPagador));

			// Campo não necessário, só associa caso != null.
			if (this.aipUf != null
					&& StringUtils.isNotBlank(this.aipUf.getSigla())) {
				this.fatConvenioSaude.setUf(this.aipUf);
			} else {
				this.fatConvenioSaude.setUf(null);
			}

			try {
				this.faturamentoApoioFacade.persistir(this.fatConvenioSaude);

			} catch (ApplicationBusinessException e) {

				fatConvenioSaude.setCodigo(null);

				e.getMessage();
				apresentarExcecaoNegocio(e);

				return null;
			}
		} else {
			this.convenioSaudePlanos = new ArrayList<FatConvenioSaudePlano>(
					this.faturamentoFacade.pesquisarPlanoPorConvenioSaude(this.fatConvenioSaude.getCodigo()));
		}

		// Ordenação por Área.
		Collections.sort(this.convenioSaudePlanos,
				new ConvenioSaudePlanoComparator());

		return PAGE_PLANOS_LIST;

	}

	/**
	 * Ordena Planos do convênio por Código.
	 * 
	 * @author riccosta
	 * 
	 */
	class ConvenioSaudePlanoComparator implements
			Comparator<FatConvenioSaudePlano> {

		@Override
		public int compare(FatConvenioSaudePlano o1, FatConvenioSaudePlano o2) {

			Byte area1 = (o1).getId().getSeq();
			Byte area2 = (o2).getId().getSeq();

			if (area1 > area2) {
				return 1;
			} else if (area1 < area2) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Método para editar o plano de saúde associado ao Convênio
	 * 
	 * @param planoPaciente
	 */
	public String editarConvenioPlano(FatConvenioSaudePlano convenioSaudePlano) {

		// TODO:Trocar getConveniosPlanos por lista, ou então dar refresh.

		this.convenioSaudePlano = convenioSaudePlano;

		if (this.convenioSaudePlano.getId().getSeq() == null) {
			if (this.fatConvenioSaude.getCodigo() != null) {
				FatConvenioSaudePlanoId id = new FatConvenioSaudePlanoId();
				id.setCnvCodigo(this.fatConvenioSaude.getCodigo());
				this.convenioSaudePlano.setId(id);
			}
		}

		if (convenioSaudePlano.getIndSituacao() != null) {
			if (convenioSaudePlano.getIndSituacao().equals(DominioSituacao.I)) {
				this.csPlanoAtivo = false;
			} else {
				this.csPlanoAtivo = true;
			}
		}

		// Obtem listas associadas;
		this.convPeriodos = new ArrayList<FatPeriodosEmissao>(this.faturamentoFacade.pesquisarPeriodosEmissaoConvenioSaudePlano(this.convenioSaudePlano));
		this.convTipoDocumentos = new ArrayList<FatConvTipoDocumentos>(this.faturamentoFacade.pesquisarConvTipoDocumentoConvenioSaudePlano(this.convenioSaudePlano));
		this.convPlanoAcomodacoes = new ArrayList<FatConvPlanoAcomodacoes>(this.faturamentoFacade.pesquisarConvPlanoAcomodocaoConvenioSaudePlano(this.convenioSaudePlano));

		this.acomodacao = null;
		this.tipoDocumento = null;		
		
		return PAGE_PLANOS_CRUD;
	}

	/**
	 * Prepara a criação de novo Planoa associado ao Convênio em questão.
	 */
	public String prepararAdicaoConvenioPlano() {

		this.convenioSaudePlano = new FatConvenioSaudePlano();

		this.csPlanoAtivo = true;

		this.convPeriodos = new ArrayList<FatPeriodosEmissao>(0);
		this.convPlanoAcomodacoes = new ArrayList<FatConvPlanoAcomodacoes>(0);
		this.convTipoDocumentos = new ArrayList<FatConvTipoDocumentos>(0);

		return PAGE_PLANOS_CRUD;
	}

	/**
	 * Cria associação entre PeriodoEmissão e Plano.
	 * 
	 * @param plano
	 */
	public String associarPeriodoEmissaoPlano() {
		try {
			this.faturamentoApoioFacade.validaPeriodos(this.convenioSaudePlano
					.getConvPeriodos(), this.convenioSaudePlano
					.getPeriodicidadeEmissao());

			this.faturamentoApoioFacade.validaPeriodo(this.diaInicio, this.diaFim,
					this.diaSemana, this.convenioSaudePlano
							.getPeriodicidadeEmissao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		FatPeriodosEmissao periodoEmissao = new FatPeriodosEmissao();

		if (this.convenioSaudePlano.getPeriodicidadeEmissao().equals(
				DominioPeriodicidade.S)) {
			periodoEmissao.setDiaSemana(this.diaSemana);
		}

		if (this.convenioSaudePlano.getPeriodicidadeEmissao().equals(
				DominioPeriodicidade.M)) {
			periodoEmissao.setDiaInicio(this.diaInicio);
			periodoEmissao.setDiaFim(this.diaFim);
		}

		FatPeriodosEmissaoId id = new FatPeriodosEmissaoId();
		periodoEmissao.setId(id);

		if (!this.contains(periodoEmissao)) {
			this.convPeriodos.add(periodoEmissao);

			// Limpa campos de entrada.
			this.diaInicio = null;
			this.diaFim = null;
			this.diaSemana = null;
		}

		return null;
	}

	/**
	 * Rotina que idetifica se objeto já está associado a lista.
	 * 
	 * @param periodoEmissao
	 * @return Boolean - associado ou não.
	 */
	private Boolean contains(FatPeriodosEmissao periodoEmissao) {
		boolean contains = false;

		for (FatPeriodosEmissao perEmissao : this.convPeriodos) {
			if (perEmissao.getDiaInicio() != null
					&& periodoEmissao.getDiaInicio() != null
					&& perEmissao.getDiaInicio().equals(
							periodoEmissao.getDiaInicio())) {
				if (perEmissao.getDiaFim() != null
						&& periodoEmissao.getDiaFim() != null
						&& perEmissao.getDiaFim().equals(
								periodoEmissao.getDiaFim())) {
					if (perEmissao.getDiaSemana() != null
							&& periodoEmissao.getDiaSemana() != null
							&& perEmissao.getDiaSemana().equals(
									periodoEmissao.getDiaSemana())) {
						contains = true;
					}
				}
			}
		}

		if (contains || this.convPeriodos.contains(periodoEmissao)) {
			return true;
		}

		return false;
	}

	/**
	 * Cria associação entre plano e convênio e documento
	 * 
	 * @param plano
	 */
	public void associarTipoDocConvenioPlano() {
		if (this.tipoDocumento != null && this.tipoDocumento.getSeq() != null) {

			FatConvTipoDocumentos convenioTipoDocumento = new FatConvTipoDocumentos();
			convenioTipoDocumento.setObrigatorio(true);
			convenioTipoDocumento.setTipoDocumento(this.tipoDocumento);
			convenioTipoDocumento.setConvenioSaudePlano(this.convenioSaudePlano);

			FatConvTipoDocumentosId id = new FatConvTipoDocumentosId();
			id.setTpdSeq(this.tipoDocumento.getSeq().byteValue());
			convenioTipoDocumento.setId(id);

			if (!this.contains(convenioTipoDocumento)) {
				this.convTipoDocumentos.add(convenioTipoDocumento);
				this.tipoDocumento = null;
			} else {
				this.tipoDocumento = null;
				apresentarMsgNegocio(Severity.WARN, "DOCUMENTO_JA_ASSOCIADO");
			}
		} else {
			apresentarMsgNegocio(Severity.WARN, "SELECIONE_TIPO_DOCUMENTO");
		}
	}

	/**
	 * Rotina que idetifica se objeto já está associado a lista.
	 * 
	 * @param convTipoDocumento
	 * @return Boolean - associado ou não.
	 */
	private Boolean contains(FatConvTipoDocumentos convTipoDocumento) {
		boolean contains = false;

		for (FatConvTipoDocumentos convTipoDoc : this.convTipoDocumentos) {
			if (convTipoDoc.getTipoDocumento() != null
					&& convTipoDocumento.getTipoDocumento() != null
					&& convTipoDoc.getTipoDocumento().getSeq().equals(
							convTipoDocumento.getTipoDocumento().getSeq())) {
				contains = true;
			}
		}

		if (contains || this.convTipoDocumentos.contains(convTipoDocumento)) {
			return true;
		}

		return false;
	}

	public void associarAcomodacaoConvenioPlano() {
		if (this.acomodacao != null && this.acomodacao.getSeq() != null) {

			FatConvPlanoAcomodacoes convPlanoAcomodacoes = new FatConvPlanoAcomodacoes();
			convPlanoAcomodacoes.setAcomodacao(this.acomodacao);
			convPlanoAcomodacoes.setConvenioSaudePlano(this.convenioSaudePlano);

			FatConvPlanoAcomodacoesId id = new FatConvPlanoAcomodacoesId();
			id.setAcmSeq(this.acomodacao.getSeq());
			convPlanoAcomodacoes.setId(id);

			if (!this.contains(convPlanoAcomodacoes)) {
				this.convPlanoAcomodacoes.add(convPlanoAcomodacoes);
				this.acomodacao = null;
			} else {
				this.acomodacao = null;
				apresentarMsgNegocio(Severity.WARN, "ACOMODACAO_JA_ASSOCIADA");
			}
		} else {
			apresentarMsgNegocio(Severity.WARN, "SELECIONE_ACOMODACAO");
		}
	}

	private Boolean contains(FatConvPlanoAcomodacoes convPlanoAcomodacao) {
		boolean contains = false;

		for (FatConvPlanoAcomodacoes convPlanoAcom : convPlanoAcomodacoes) {
			if (convPlanoAcom.getAcomodacao() != null
					&& convPlanoAcomodacao.getAcomodacao() != null
					&& convPlanoAcom.getAcomodacao().getSeq().equals(
							convPlanoAcomodacao.getAcomodacao().getSeq())) {
				contains = true;
			}
		}

		if (contains || this.convPlanoAcomodacoes.contains(convPlanoAcomodacoes)) {
			return true;
		}

		return false;
	}

	/**
	 * Remove associação entre plano, convênio e tipo de documento.
	 * 
	 * @param tipoDocumento
	 */
	public void removerTipoDocConvenioPlano(
			FatConvTipoDocumentos convenioTipoDocumento) {
		this.convTipoDocumentos.remove(convenioTipoDocumento);
	}

	/**
	 * Remove associação entre plano, convênio e acomodação.
	 * 
	 * @param convenioPlanoAcomodacao
	 */
	public void removerAcomodacaoConvenioPlano(
			FatConvPlanoAcomodacoes convenioPlanoAcomodacao) {
		this.convPlanoAcomodacoes.remove(convenioPlanoAcomodacao);
	}

	/**
	 * Remove associação entre plano e periodo de Emissão.
	 * 
	 * @param convenioPlanoAcomodacao
	 */
	public void removerPeriodoEmissaoPlano(FatPeriodosEmissao periodosEmissao) {
		this.convPeriodos.remove(periodosEmissao);
	}
	
	public List<AipUfs> pesquisarPorSiglaNome(String paramPesquisa) {
		return this.returnSGWithCount(this.cadastrosBasicosPacienteFacade.pesquisarPorSiglaNome(paramPesquisa),obterCountUfPorSiglaNome(paramPesquisa));
	}

	public Long obterCountUfPorSiglaNome(String paramPesquisa) {
		return this.cadastrosBasicosPacienteFacade.obterCountUfPorSiglaNome(paramPesquisa);
	}
	
	public List<AacPagador> pesquisarPagadores(String filtro) {
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarPagadores((String) filtro),pesquisarPagadoresCount(filtro));
	}

	public Long pesquisarPagadoresCount(String filtro) {
		return this.ambulatorioFacade.pesquisarPagadoresCount((String) filtro);
	}
	
	// ### GETs e SETs ###

	public Short getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public FatConvenioSaude getFatConvenioSaude() {
		return this.fatConvenioSaude;
	}

	public void setFatConvenioSaude(FatConvenioSaude fatConvenioSaude) {
		this.fatConvenioSaude = fatConvenioSaude;
	}

	public Boolean getCsAtivo() {
		return this.csAtivo;
	}

	public void setCsAtivo(Boolean csAtivo) {
		this.csAtivo = csAtivo;
	}

	public AacPagador getAacPagador() {
		return this.aacPagador;
	}

	public void setAacPagador(AacPagador aacPagador) {
		this.aacPagador = aacPagador;
	}

	public AipUfs getAipUf() {
		return this.aipUf;
	}

	public void setAipUf(AipUfs aipUf) {
		this.aipUf = aipUf;
	}

	public boolean isOperacaoConcluida() {
		return this.operacaoConcluida;
	}

	public void setOperacaoConcluida(boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return this.convenioSaudePlano;
	}

	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}

	public String getSeqDescTipoDocLov() {
		return this.seqDescTipoDocLov;
	}

	public void setSeqDescTipoDocLov(String seqDescTipoDocLov) {
		this.seqDescTipoDocLov = seqDescTipoDocLov;
	}

	public FatTiposDocumento getTipoDocumento() {
		return this.tipoDocumento;
	}

	public void setTipoDocumento(FatTiposDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public List<FatTiposDocumento> getTiposDocumentos() {
		return this.tiposDocumentos;
	}

	public void setTiposDocumentos(List<FatTiposDocumento> tiposDocumentos) {
		this.tiposDocumentos = tiposDocumentos;
	}

	public Integer getSeqAcomodacao() {
		return this.seqAcomodacao;
	}

	public void setSeqAcomodacao(Integer seqAcomodacao) {
		this.seqAcomodacao = seqAcomodacao;
	}

	public String getSeqDescAcomodacao() {
		return this.seqDescAcomodacao;
	}

	public void setSeqDescAcomodacao(String seqDescAcomodacao) {
		this.seqDescAcomodacao = seqDescAcomodacao;
	}

	public AinAcomodacoes getAcomodacao() {
		return this.acomodacao;
	}

	public void setAcomodacao(AinAcomodacoes acomodacao) {
		this.acomodacao = acomodacao;
	}

	public List<AinAcomodacoes> getAcomodacoes() {
		return this.acomodacoes;
	}

	public void setAcomodacoes(List<AinAcomodacoes> acomodacoes) {
		this.acomodacoes = acomodacoes;
	}

	public Boolean getCsPlanoAtivo() {
		return this.csPlanoAtivo;
	}

	public void setCsPlanoAtivo(Boolean csPlanoAtivo) {
		this.csPlanoAtivo = csPlanoAtivo;
	}

	public Integer getDiaInicio() {
		return this.diaInicio;
	}

	public void setDiaInicio(Integer diaInicio) {
		this.diaInicio = diaInicio;
	}

	public Integer getDiaFim() {
		return this.diaFim;
	}

	public void setDiaFim(Integer diaFim) {
		this.diaFim = diaFim;
	}

	public Integer getDiaSemana() {
		return this.diaSemana;
	}

	public void setDiaSemana(Integer diaSemana) {
		this.diaSemana = diaSemana;
	}

	public DominioPeriodicidade getPeriodicidade() {
		return this.periodicidade;
	}

	public void setPeriodicidade(DominioPeriodicidade periodicidade) {
		this.periodicidade = periodicidade;
	}

	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Short getSeqTipoDoc() {
		return seqTipoDoc;
	}

	public void setSeqTipoDoc(Short seqTipoDoc) {
		this.seqTipoDoc = seqTipoDoc;
	}

	public List<FatPeriodosEmissao> getConvPeriodos() {
		return convPeriodos;
	}

	public void setConvPeriodos(List<FatPeriodosEmissao> convPeriodos) {
		this.convPeriodos = convPeriodos;
	}

	public List<FatConvTipoDocumentos> getConvTipoDocumentos() {
		return convTipoDocumentos;
	}

	public void setConvTipoDocumentos(
			List<FatConvTipoDocumentos> convTipoDocumentos) {
		this.convTipoDocumentos = convTipoDocumentos;
	}

	public List<FatConvPlanoAcomodacoes> getConvPlanoAcomodacoes() {
		return convPlanoAcomodacoes;
	}

	public void setConvPlanoAcomodacoes(
			List<FatConvPlanoAcomodacoes> convPlanoAcomodacoes) {
		this.convPlanoAcomodacoes = convPlanoAcomodacoes;
	}

	public List<FatConvenioSaudePlano> getConvenioSaudePlanos() {
		return convenioSaudePlanos;
	}

	public void setConvenioSaudePlanos(
			List<FatConvenioSaudePlano> convenioSaudePlanos) {
		this.convenioSaudePlanos = convenioSaudePlanos;
	}
}