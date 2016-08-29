package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioFatorRh;
import br.gov.mec.aghu.dominio.DominioGrupoSanguineo;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.MtxContatoPacientes;
import br.gov.mec.aghu.model.MtxDoencaBases;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.PacienteAguardandoTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplantadosOrgaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe controller da estoria #41796 - Incluir Paciente na Lista de Transplante - Rins
 *
 * @author rafael.silvestre
 */
public class IncluirPacienteListaTransplanteController extends ActionController{

	/**
	 * SERIAL ID
	 */
	private static final long serialVersionUID = -6640406526538832518L;
	
	private static final String ANOS = " anos";
	private static final String MASCARA_TELEFONE_10 = "(##)####-####";
	private static final String MASCARA_TELEFONE_11 = "(##)#####-####";
	private static final String MENSAGEM_SUCESSO_INCLUSAO = "MENSAGEM_SUCESSO_INCLUSAO_TRANSPLANTE_ORGAOS";
	private static final String MENSAGEM_SUCESSO_EDICAO = "MENSAGEM_SUCESSO_EDICAO_TRANSPLANTE_ORGAOS";
	private static final String MENSAGEM_SUCESSO_INCLUSAO_CONTATO = "MENSAGEM_SUCESSO_INCLUSAO_CONTATO_PACIENTE";
	private static final String MENSAGEM_SUCESSO_EDICAO_CONTATO = "MENSAGEM_SUCESSO_EDICAO_CONTATO_PACIENTE";
	private static final String MENSAGEM_SUCESSO_EXCLUSAO_CONTATO = "MENSAGEM_SUCESSO_EXCLUSAO_CONTATO_PACIENTE";
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	private AipPacientes paciente;
	private AipRegSanguineos regSanguineo;
	private MtxTransplantes transplante;
	private MtxTransplantes transplantePesquisa;
	private MtxContatoPacientes contatoPaciente;
	private MtxContatoPacientes contatoSelecionado;
	
	private DominioGrupoSanguineo grupoSanguineo;
	private DominioFatorRh fatorRh;
	
	private List<MtxDoencaBases> listaDoencaBases;
	private List<MtxContatoPacientes> listaContatoPaciente; // Para fins de exibição
	private List<MtxContatoPacientes> listaContatoPacienteIncluidos;
	private List<MtxContatoPacientes> listaContatoPacienteExcluidos;
	
	private Integer pacCodigo;
	private PacienteAguardandoTransplanteOrgaoVO itemSelecionadoaba1;
	private PacienteTransplantadosOrgaoVO itemSelecionadoAba2;	
	private String telaAnterior;
	private String idadeAtual;
	private String idadeIngresso;
	private String prontuario;
	private String contatoNome;
	private String contatoTelefone;
	
	private boolean orgaoSelecionado;
	private boolean editandoTransplante;
	private boolean editandoContatoPaciente;
	
	private Boolean habilitaGrupoSanguinio = Boolean.TRUE;
	private Boolean habilitaFator = Boolean.TRUE;
	
	private Integer seqTransplante;

	@PostConstruct
	public void init() {
		this.begin(conversation, true);
	}
	
	/**
	 * Método invocado ao acessar a tela, determina valores iniciais para os componentes que compôe a mesma.
	 */
	public void iniciar() {
		if (this.pacCodigo != null) {
			inicializarDadosPaciente(this.pacCodigo);
			inicializarRegSanguineo(this.pacCodigo);
			inicializarListasContatos(this.pacCodigo);
		}
		this.idadeIngresso = null;
		this.transplante = new MtxTransplantes();
		this.transplantePesquisa = new MtxTransplantes();
		this.transplante.setTipoOrgao(DominioTipoOrgao.R);
		if(this.itemSelecionadoaba1 != null){
			this.transplantePesquisa = pacienteFacade.pesquisarTransplantePaciente(this.itemSelecionadoaba1.getCodigoReceptor(), this.itemSelecionadoaba1.getSeqTransplante());			
		}else if(this.itemSelecionadoAba2 != null){
			this.transplantePesquisa = pacienteFacade.pesquisarTransplantePaciente(this.itemSelecionadoAba2.getCodigoReceptor(), this.itemSelecionadoAba2.getSeqTransplante());
		}
		if (this.seqTransplante != null) {
			this.transplantePesquisa = pacienteFacade.pesquisarTransplantePaciente(this.pacCodigo, this.seqTransplante);
		}
		if(transplantePesquisa.getSeq() != null){
			transplante = transplantePesquisa;
		}
		this.habilitarCampoDoencaBase();
		this.contatoNome = null;
		this.contatoTelefone = null;
		this.contatoPaciente = new MtxContatoPacientes();
		this.editandoContatoPaciente = false;
	}	

	/**
	 * Carrega os dados do paciente ao acessar a página.
	 * 
	 * @param pacCodigo {@link Integer}
	 */
	private void inicializarDadosPaciente(Integer pacCodigo) {
		this.paciente = this.pacienteFacade.obterPacientePorCodigo(pacCodigo);
		if (this.paciente != null && this.paciente.getDtNascimento() != null) {
			this.idadeAtual = this.calcularIdateAtual(this.paciente.getDtNascimento());
		} else {
			this.idadeAtual = null;
		}
		if (this.paciente != null && this.paciente.getProntuario() != null) {
			this.prontuario = this.formatarMascaraProntuario(this.paciente.getProntuario());
		} else {
			this.prontuario = null;
		}
	}

	/**
	 * Carrega os dados do Registro Sanguineo do paciente ao acessar a pagina.
	 *  
	 * @param pacCodigo {@link Integer}
	 */
	private void inicializarRegSanguineo(Integer pacCodigo) {
		habilitaGrupoSanguinio = true;
		habilitaFator = true;
		this.regSanguineo = this.pacienteFacade.obterRegSanguineoPorCodigoPaciente(pacCodigo); 
		if (this.regSanguineo != null) {
			
			if(this.regSanguineo.getGrupoSanguineo() != null){
				this.grupoSanguineo = this.obterGrupoSanguineoPorCodigo(this.regSanguineo.getGrupoSanguineo());
			}else{
				popularGrupoSanguineoExamesRealizados();
			}
			
			if(this.regSanguineo.getFatorRh() != null){
				this.fatorRh = this.obterFatorRhPorCodigo(this.regSanguineo.getFatorRh());
			}else{
				popularFatorRhExamesRealizados();
			}
			
		}else{//se C2 não retornou nada...
			this.regSanguineo = new AipRegSanguineos();
			popularGrupoSanguineoExamesRealizados();
			popularFatorRhExamesRealizados();
		}
	}
	
	private void popularFatorRhExamesRealizados(){
		String fator = examesFacade.obterFatorRhExamesRealizados(pacCodigo);
		try{
			if(StringUtils.isNotEmpty(fator)){
				this.fatorRh = obterFatorRhPorCodigo(fator);
				habilitaFator = Boolean.FALSE;
			}else{
				this.fatorRh = null;
			}
		}catch(IllegalArgumentException e){
			fatorRh = null;
		}
	}
	
	private void popularGrupoSanguineoExamesRealizados(){
		String grupo = examesFacade.obterFatorFatorSanguinioExamesRealizados(pacCodigo);	
		try{
			if(StringUtils.isNotEmpty(grupo)){
				this.grupoSanguineo = DominioGrupoSanguineo.valueOf(grupo);
				habilitaGrupoSanguinio = Boolean.FALSE;
			}else{
				this.grupoSanguineo = null;
			}
		} catch(IllegalArgumentException e){
			grupoSanguineo = null;
		}
	}

	/**
	 * Determina valores iniciais para as listas de contatos do paciente.
	 *  
	 * @param pacCodigo {@link Integer}
	 */
	private void inicializarListasContatos(Integer pacCodigo) {
		this.listaContatoPaciente = this.transplanteFacade.obterListaContatoPacientesPorCodigoPaciente(pacCodigo);
		if (this.listaContatoPaciente == null) {
			this.listaContatoPaciente = new ArrayList<MtxContatoPacientes>();
		}
		if (this.listaContatoPacienteIncluidos == null) {
			this.listaContatoPacienteIncluidos = new ArrayList<MtxContatoPacientes>();
		}
		if (this.listaContatoPacienteExcluidos == null) {
			this.listaContatoPacienteExcluidos = new ArrayList<MtxContatoPacientes>();
		}
	}
	
	/**
	 * Formata número do prontuario para exibir conforme mascara sugerida.
	 * 
	 * @param prontuario {@link Integer}
	 * @return {@link String} Número de Prontuario Formatado
	 */
	private String formatarMascaraProntuario(Integer prontuario) {
		
		String valor = prontuario.toString();
		int tamanho = valor.length();
		String mascara = "";
		
		for (int i = 0; i < tamanho; i++) {
			if (i == tamanho-1) {
				mascara = mascara.concat("-#"); // ULTIMO DIGITO
			} else {
				mascara = mascara.concat("#"); // DEMAIS DIGITOS
			}
		}
		
		return this.inserirMascara(valor, mascara);
	}

	/**
	 * Obtem Dominio do Grupo Sanguineo equivalente ao código obtido do Registro Sanguineo. 
	 * 
	 * @param codigo {@link String}
	 * @return {@link DominioGrupoSanguineo}
	 */
	private DominioGrupoSanguineo obterGrupoSanguineoPorCodigo(String codigo) {
		if(codigo != null){
			switch (codigo) {
			case "A": 
				return DominioGrupoSanguineo.A;
			case "B": 
				return DominioGrupoSanguineo.B;
			case "AB": 
				return DominioGrupoSanguineo.AB;
			case "O": 
				return DominioGrupoSanguineo.O;
			default:
				return null;	
			}
		}
		return null;
	}
	
	/**
	 * Obtem Dominio do Fator RH equivalente ao código obtido do Registro Sanguineo.
	 * 
	 * @param codigo {@link String}
	 * @return {@link DominioFatorRh}
	 */
	private DominioFatorRh obterFatorRhPorCodigo(String codigo) {
		if(codigo != null){
			switch (codigo) {
			case "+": 
				return DominioFatorRh.P;
			case "-": 
				return DominioFatorRh.N;
			default:
				return null;	
			}
		}
		return null;
	}
	
	/**
	 * Calcula a diferença em anos entre a data de nascimento do paciente com a data atual.
	 * 
	 * @param dtNascimento
	 * @return
	 */
	private String calcularIdateAtual(Date dtNascimento) {
		
		Integer idade = DateUtil.obterQtdAnosEntreDuasDatas(dtNascimento, new Date());
		
		return idade.toString().concat(ANOS);
	}
	
	/**
	 * Calcula a diferença em anos entre a data de nascimento do paciente com a data de ingresso.
	 */
	public void calcularIdadeIngresso() {
		
		if (this.transplante != null && this.transplante.getDataIngresso() != null && this.paciente != null && this.paciente.getDtNascimento() != null) {
			
			Integer idade = DateUtil.obterQtdAnosEntreDuasDatas(this.paciente.getDtNascimento(), this.transplante.getDataIngresso());
			
			this.idadeIngresso = idade.toString().concat(ANOS);
		}
	}
	
	/**
	 * Habilita o combo Doença Base baseado no orgão selecionado.
	 */
	public void habilitarCampoDoencaBase() {

		if (this.transplante != null && this.transplante.getTipoOrgao() != null) {
		
			this.listaDoencaBases = this.transplanteFacade.obterListaDoencaBasePorTipoOrgao(this.transplante.getTipoOrgao());
			this.orgaoSelecionado = true;
		} else {
			
			this.listaDoencaBases = new ArrayList<MtxDoencaBases>();
			this.orgaoSelecionado = false;
		}
	}
	
	/**
	 * Lista SuggestionBox AghCid
	 */
	public List<AghCid> pesquisarCid(String pesquisa) {
		return this.returnSGWithCount(this.transplanteFacade.pesquisarCidPorSeqCodDescricao(pesquisa),this.transplanteFacade.pesquisarCidPorSeqCodDescricaoCount(pesquisa));
	}
	
	/**
	 * Ação do botão Adicionar, inclui novo registro a grid de Contatos do Paciente.
	 */
	public void adicionarContatoPaciente() {
		
		if (this.contatoNome != null && this.contatoTelefone != null) {
			
			this.contatoPaciente = new MtxContatoPacientes();
			this.contatoPaciente.setNome(this.contatoNome);
			this.contatoPaciente.setTelefone(this.removerMascaraTelefone(this.contatoTelefone));
			
			if (this.listaContatoPaciente == null) {
				this.listaContatoPaciente = new ArrayList<MtxContatoPacientes>();
			}
			this.listaContatoPaciente.add(this.contatoPaciente);
			
			if (this.listaContatoPacienteIncluidos == null) {
				this.listaContatoPacienteIncluidos = new ArrayList<MtxContatoPacientes>();
			}
			this.listaContatoPacienteIncluidos.add(this.contatoPaciente);
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_INCLUSAO_CONTATO);
		}
		
		this.limparCamposContatoPaciente();
	}
	
	/**
	 * Ação do link Editar, carrega os dados do Contato do Paciente para alteração.
	 */
	public void editarContatoPaciente() {
		
		if (this.contatoSelecionado != null) {
			this.contatoPaciente = this.contatoSelecionado;
			this.contatoNome = this.contatoSelecionado.getNome();
			this.contatoTelefone = this.inserirMascaraTelefone(this.contatoSelecionado.getTelefone());
		}
	}
	
	/**
	 * Ação do botão Sim do modal de exclusão, confirma a exclusão do Contato do Paciente.
	 */
	public void excluirContatoPaciente() {
		
		if (this.contatoSelecionado != null) {
			if (this.contatoSelecionado.getSeq() != null) {
				if (this.listaContatoPacienteExcluidos == null) {
					this.listaContatoPacienteExcluidos = new ArrayList<MtxContatoPacientes>();
				}
				this.listaContatoPacienteExcluidos.add(this.contatoSelecionado);
			}
			this.listaContatoPacienteIncluidos.remove(this.contatoSelecionado);
			this.listaContatoPaciente.remove(this.contatoSelecionado);
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EXCLUSAO_CONTATO);
		}
	}
	
	/**
	 * Ação do botão Alterar, edita as informações do Contato do Paciente.
	 */
	public void alterarContatoPaciente() {
		
		if (this.contatoSelecionado != null) {
			this.listaContatoPacienteIncluidos.remove(this.contatoSelecionado);
			this.listaContatoPaciente.remove(this.contatoSelecionado);
		}
		
		if (this.contatoPaciente != null && this.contatoNome != null && this.contatoTelefone != null) {
			this.contatoPaciente.setNome(this.contatoNome);
			this.contatoPaciente.setTelefone(this.removerMascaraTelefone(this.contatoTelefone));

			this.listaContatoPacienteIncluidos.add(this.contatoPaciente);
			this.listaContatoPaciente.add(this.contatoPaciente);
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EDICAO_CONTATO);
		}
		
		this.limparCamposContatoPaciente();
	}
	
	/**
	 * Ação do botão Cancelar Edição, limpa os campos da grid de Contatos do Paciente.
	 */
	public void cancelarEdicaoContatoPaciente() {
		
		this.limparCamposContatoPaciente();
	}
	
	/**
	 * Limpa os campos referentes ao cadastro/edição de Contato do Paciente.
	 */
	private void limparCamposContatoPaciente() {
		
		this.contatoSelecionado = null;
		this.contatoPaciente = new MtxContatoPacientes();
		this.contatoNome = null;
		this.contatoTelefone = null;
		this.editandoContatoPaciente = false;
	}
	
	/**
	 * Formata o numero de telefone do contato retornando-o com a mascara.
	 * 
	 * @param numeroTelefone do Contato do Paciente.
	 * @return Numero do Telefone com mascara.
	 */
	public String formatarTelefoneContatoPaciente(Long numeroTelefone) {
		
		return this.inserirMascaraTelefone(numeroTelefone);
	}
	
	/**
	 * Trunca o nome do contato do paciente que ultrapasse o limite de caracteres da coluna.
	 * 
	 * @param nomeContato Nome do Contato do Paciente
	 * @param tamanho Número máximo de caracteres a ser exibido
	 * @return Nome do Contato truncado
	 */
	public String obterHintNomeContatoPaciente(String nomeContato, int tamanho) {
		if (StringUtils.isNotBlank(nomeContato) && nomeContato.length() > tamanho) {
			nomeContato = StringUtils.abbreviate(nomeContato, tamanho);
		}
		return nomeContato;
	}
	
	/**
	 * Remove mascara do Telefone e converte de String para Long.
	 * 
	 * @param telefoneComMascara {@link String}
	 * @return {@link Long} Número do Telefone
	 */
	private Long removerMascaraTelefone(String telefoneComMascara) {
		
		return Long.valueOf(this.removerMascara(telefoneComMascara));
	}
	
	/**
	 * Insere mascara do Telefone e converte de Long para String.
	 * 
	 * @param telefoneSemMascara {@link Long}
	 * @return {@link String} Número do Telefone.
	 */
	private String inserirMascaraTelefone(Long telefoneSemMascara) {
		
		String telefone = telefoneSemMascara.toString();
		
		if (telefone.length() == 10) {
			return this.inserirMascara(telefone, MASCARA_TELEFONE_10);
		}
		
		return this.inserirMascara(telefone, MASCARA_TELEFONE_11);
	}
	
	/**
	 * Ação do botão Gravar, salva entidade Transplante e lista de contatos do paciente.
	 */
	public void gravar() {
		
		try {
		
			if (this.transplante != null) {
				this.regSanguineo.setGrupoSanguineo(this.grupoSanguineo.getDescricao());
				this.regSanguineo.setFatorRh(this.fatorRh.getDescricao());
				this.transplanteFacade.salvarTransplanteComManutencaoContatosRegSanguineo(
						this.transplante, this.regSanguineo, this.paciente,
						this.listaContatoPacienteIncluidos, this.listaContatoPacienteExcluidos);
			
				if (this.editandoTransplante) {
					apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_EDICAO);
				} else {
					apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_INCLUSAO);
				}
			}
			
		} catch (ApplicationBusinessException e) {
			
			apresentarExcecaoNegocio(e);
		} catch (BaseListException e) {
			
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão Voltar, redireciona para a tela anterior.
	 * 
	 * @return Nome da Tela Anterior
	 */
	public String voltar() {
		return this.telaAnterior;
	}
	
	/**
     * Insere a máscara de formatação no valor da String informada.<br /><tt>Ex.: inserirMascara("11111111111",
     * "###.###.###-##")</tt>.
     * 
     * @param valor {@link String} que será manipulada.
     * @param mascara Máscara que será aplicada.
     * @return Valor com a máscara de formatação.
     */
    private String inserirMascara(String valor, String mascara) {

        String novoValor = "";
        int posicao = 0;

        for (int i = 0; mascara.length() > i; i++) {
            if (mascara.charAt(i) == '#') {
                if (valor.length() > posicao) {
                    novoValor = novoValor.concat(String.valueOf(valor.charAt(posicao)));
                    posicao++;
                } else {
                    break;
                }
            } else {
                if (valor.length() > posicao) {
                    novoValor = novoValor.concat(String.valueOf(mascara.charAt(i)));
                } else {
                    break;
                }
            }
        }
        return novoValor;
    }
    
    /**
     * Remove as máscaras ou qualquer caractere que não seja um número da String informada.
     * 
     * @param valor {@link String} que será manipulada.
     * @return String contendo apenas números.
     */
    private String removerMascara(String valor) {

        Pattern replace = Pattern.compile("[^0-9]");
        Matcher matcher = replace.matcher(valor);

        return matcher.replaceAll("");
    }
    
	/**
	 * 
	 * GETs and SETs 
	 * 
	 */
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	public String getTelaAnterior() {
		return telaAnterior;
	}
	
	public void setTelaAnterior(String telaAnterior) {
		this.telaAnterior = telaAnterior;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AipRegSanguineos getRegSanguineo() {
		return regSanguineo;
	}

	public void setRegSanguineo(AipRegSanguineos regSanguineo) {
		this.regSanguineo = regSanguineo;
	}

	public MtxTransplantes getTransplante() {
		return transplante;
	}

	public void setTransplante(MtxTransplantes transplante) {
		this.transplante = transplante;
	}

	public String getIdadeAtual() {
		return idadeAtual;
	}

	public void setIdadeAtual(String idadeAtual) {
		this.idadeAtual = idadeAtual;
	}

	public String getIdadeIngresso() {
		return idadeIngresso;
	}

	public void setIdadeIngresso(String idadeIngresso) {
		this.idadeIngresso = idadeIngresso;
	}

	public List<MtxDoencaBases> getListaDoencaBases() {
		return listaDoencaBases;
	}

	public void setListaDoencaBases(List<MtxDoencaBases> listaDoencaBases) {
		this.listaDoencaBases = listaDoencaBases;
	}

	public boolean isOrgaoSelecionado() {
		return orgaoSelecionado;
	}

	public void setOrgaoSelecionado(boolean orgaoSelecionado) {
		this.orgaoSelecionado = orgaoSelecionado;
	}

	public MtxContatoPacientes getContatoSelecionado() {
		return contatoSelecionado;
	}

	public void setContatoSelecionado(MtxContatoPacientes contatoSelecionado) {
		this.contatoSelecionado = contatoSelecionado;
	}

	public boolean isEditandoContatoPaciente() {
		return editandoContatoPaciente;
	}

	public void setEditandoContatoPaciente(boolean editandoContatoPaciente) {
		this.editandoContatoPaciente = editandoContatoPaciente;
	}

	public List<MtxContatoPacientes> getListaContatoPacienteExcluidos() {
		return listaContatoPacienteExcluidos;
	}

	public void setListaContatoPacienteExcluidos(List<MtxContatoPacientes> listaContatoPacienteExcluidos) {
		this.listaContatoPacienteExcluidos = listaContatoPacienteExcluidos;
	}

	public List<MtxContatoPacientes> getListaContatoPacienteIncluidos() {
		return listaContatoPacienteIncluidos;
	}

	public void setListaContatoPacienteIncluidos(List<MtxContatoPacientes> listaContatoPacienteIncluidos) {
		this.listaContatoPacienteIncluidos = listaContatoPacienteIncluidos;
	}

	public List<MtxContatoPacientes> getListaContatoPaciente() {
		return listaContatoPaciente;
	}

	public void setListaContatoPaciente(List<MtxContatoPacientes> listaContatoPaciente) {
		this.listaContatoPaciente = listaContatoPaciente;
	}

	public String getContatoNome() {
		return contatoNome;
	}

	public void setContatoNome(String contatoNome) {
		this.contatoNome = contatoNome;
	}

	public String getContatoTelefone() {
		return contatoTelefone;
	}

	public void setContatoTelefone(String contatoTelefone) {
		this.contatoTelefone = contatoTelefone;
	}

	public MtxContatoPacientes getContatoPaciente() {
		return contatoPaciente;
	}

	public void setContatoPaciente(MtxContatoPacientes contatoPaciente) {
		this.contatoPaciente = contatoPaciente;
	}

	public DominioGrupoSanguineo getGrupoSanguineo() {
		return grupoSanguineo;
	}

	public void setGrupoSanguineo(DominioGrupoSanguineo grupoSanguineo) {
		this.grupoSanguineo = grupoSanguineo;
	}

	public DominioFatorRh getFatorRh() {
		return fatorRh;
	}

	public void setFatorRh(DominioFatorRh fatorRh) {
		this.fatorRh = fatorRh;
	}

	public boolean isEditandoTransplante() {
		return editandoTransplante;
	}

	public void setEditandoTransplante(boolean editandoTransplante) {
		this.editandoTransplante = editandoTransplante;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public MtxTransplantes getTransplantePesquisa() {
		return transplantePesquisa;
	}

	public void setTransplantePesquisa(MtxTransplantes transplantePesquisa) {
		this.transplantePesquisa = transplantePesquisa;
	}

	public PacienteTransplantadosOrgaoVO getItemSelecionadoAba2() {
		return itemSelecionadoAba2;
	}

	public void setItemSelecionadoAba2(PacienteTransplantadosOrgaoVO itemSelecionadoAba2) {
		this.itemSelecionadoAba2 = itemSelecionadoAba2;
	}
	
	public PacienteAguardandoTransplanteOrgaoVO getItemSelecionadoaba1() {
		return itemSelecionadoaba1;
	}

	public void setItemSelecionadoaba1(
			PacienteAguardandoTransplanteOrgaoVO itemSelecionadoaba1) {
		this.itemSelecionadoaba1 = itemSelecionadoaba1;
	}
	public Integer getSeqTransplante() {
		return seqTransplante;
	}
	public void setSeqTransplante(Integer seqTransplante) {
		this.seqTransplante = seqTransplante;
	}
	
	public Boolean getHabilitaGrupoSanguinio() {
		return habilitaGrupoSanguinio;
	}

	public void setHabilitaGrupoSanguinio(Boolean habilitaGrupoSanguinio) {
		this.habilitaGrupoSanguinio = habilitaGrupoSanguinio;
	}

	public Boolean getHabilitaFator() {
		return habilitaFator;
	}

	public void setHabilitaFator(Boolean habilitaFator) {
		this.habilitaFator = habilitaFator;
	}
}
