package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.TipoSalaVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.vo.MvtoSalaCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcMvtoSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.MbcTipoSala;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class SalaCirurgicaController extends ActionController {

	
	private static final Log LOG = LogFactory.getLog(SalaCirurgicaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -7671825581422329024L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private String nomeSalaCirurgica;
	
	private MbcSalaCirurgica salaCirurgicaHistorico;

	private MbcSalaCirurgica salaCirurgica;
	
	private List<MbcSalaCirurgica> salasCirurgicas;
	
	private List<MvtoSalaCirurgicaVO> listaHistoricoSalaCirurgica;
	
	//Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;
	
	private boolean exibirPesquisa;
	
	private boolean cameFromEdit = false;
	
	private DominioSimNao monitor; 

	
	
	private final String PAGE_CRUD_SALA_CIRURGICA = "salaCirurgicaCRUD";
	private final String PAGE_MAPEAMENTO_SALA_CIRURGICA = "manterMapeamentoSalas";
	
	public List<AghUnidadesFuncionais> listarUnidades(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroFacade.buscarUnidadesFuncionaisCirurgia(objPesquisa),listarUnidadesCount(objPesquisa));
	}
	
	public Long listarUnidadesCount(String objPesquisa) {
		return blocoCirurgicoCadastroFacade.contarUnidadesFuncionaisCirurgia(salaCirurgica.getUnidadeFuncional());
	}
	
	public List<MbcTipoSala> listarTipoSala(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroFacade.buscarTipoSalaAtivasPorCodigoOuDescricao(objPesquisa),listarTipoSalaCount(objPesquisa));
	}
	
	public Long listarTipoSalaCount(String objPesquisa) {
		return blocoCirurgicoCadastroFacade.contarTipoSalaAtivasPorCodigoOuDescricao(objPesquisa);
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */ 
	/*public void pesquisar() {
		exibirPesquisa = false;
		exibirBotaoNovo = true;
		salasCirurgicas = buscarSalaCirurgica();
		this.listaHistoricoSalaCirurgica = new ArrayList<MvtoSalaCirurgicaVO>();
		
		if(!salasCirurgicas.isEmpty()){
			exibirPesquisa = true;
		}
	}*/
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.limparPesquisa();
	}

	/*public void inicio(){
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterTipoAutorizacao", "alterar");
		this.getDataModel().setUserRemovePermission(permissao);
		this.getDataModel().setUserEditPermission(permissao);
		
	}*/
	
	public void pesquisar() {
		exibirPesquisa = false;
		exibirBotaoNovo = true;
		salasCirurgicas = buscarSalaCirurgica();
		this.listaHistoricoSalaCirurgica = new ArrayList<MvtoSalaCirurgicaVO>();
		
		if(!salasCirurgicas.isEmpty()){
			exibirPesquisa = true;
		}
	}

	public void limparPesquisa() {
		//Limpa filtro
		exibirBotaoNovo = false;
		setSalasCirurgicas(null);
		this.setSalaCirurgica(new MbcSalaCirurgica());
		this.getSalaCirurgica().setNome(null);
		this.getSalaCirurgica().setSituacao(null);
		this.getSalaCirurgica().setVisivelMonitor(null);
		this.getSalaCirurgica().setMbcTipoSala(null);
		this.getSalaCirurgica().setUnidadeFuncional(this.carregarUnidadeFuncionalInicial());
		this.getSalaCirurgica().setId(new MbcSalaCirurgicaId());
		this.setMonitor(null);
		this.limparHistoricoSalaCirurgica();
		
	}

	
	public String editar(){
		return PAGE_CRUD_SALA_CIRURGICA;
	}
	
	public String novo(){
		return PAGE_CRUD_SALA_CIRURGICA;
	}	
	
	public String mapeamento(){
		return PAGE_MAPEAMENTO_SALA_CIRURGICA;
	}	
	
	
	
	
	public void limparHistoricoSalaCirurgica(){
		this.setListaHistoricoSalaCirurgica(new ArrayList<MvtoSalaCirurgicaVO>());
	}
	
	
	/*public void inicio() {
		if(cameFromEdit){
			pesquisar();
			cameFromEdit = false;
		} else {
			cameFromEdit = true;
			this.limparPesquisa();
		}
	}*/
	/**
	 * abrevia strings(nome, descrição) para apresentação na tela
	 * @param str
	 * @param maxWidth
	 * @return
	 */
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	
	
	private List<MbcSalaCirurgica> buscarSalaCirurgica(){
		if(this.monitor!=null){
			if(this.monitor.equals(DominioSimNao.S)){
				salaCirurgica.setVisivelMonitor(true);
			}else{
				salaCirurgica.setVisivelMonitor(false);
			}
		}else{
			salaCirurgica.setVisivelMonitor(null);
		}
		return blocoCirurgicoCadastroFacade.buscarSalaCirurgica(salaCirurgica);
	}
	
	public  List<MvtoSalaCirurgicaVO> obterHistoricoSalaCirurgica(Short seqp, Short unfSeq){
        List<MbcMvtoSalaCirurgica> mvtos = blocoCirurgicoCadastroFacade.buscarHistoricoSalaCirurgica(seqp, unfSeq);
        List<MvtoSalaCirurgicaVO> mvtosSala = new ArrayList<MvtoSalaCirurgicaVO>();
        if(mvtos!=null){
            for (MbcMvtoSalaCirurgica mbcMvtoSalaCirurgica : mvtos) {
                SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String criadoEm ="";
                String alteradoEm ="";
                String nome="";
                RapPessoasFisicas pesFis=null;
                if(mbcMvtoSalaCirurgica.getDtFimMvto()!=null){
                    pesFis = registroColaboradorFacade.obterRapPessoasFisicasPorServidor(mbcMvtoSalaCirurgica.getRapServidoresByMbcMscSerFk2().getId());
                    nome = pesFis != null ? pesFis.getNome() : "";
                    alteradoEm = "Alterado em: ".concat(df2.format(mbcMvtoSalaCirurgica.getAlteradoEm()))
                            .concat(", por: ").concat(nome);
                }
                pesFis = registroColaboradorFacade.obterRapPessoasFisicasPorServidor(mbcMvtoSalaCirurgica.getRapServidoresByMbcMscSerFk1().getId());
                nome = pesFis != null ? pesFis.getNome() : "";
                criadoEm = "Incluido em :  ".concat(df2.format(mbcMvtoSalaCirurgica.getCriadoEm()))
                        .concat(", por: ").concat(nome);
                

                TipoSalaVO tipoSala = new TipoSalaVO();
                
                tipoSala.setDescricao(mbcMvtoSalaCirurgica.getMbcTipoSala() != null ? mbcMvtoSalaCirurgica.getMbcTipoSala().getDescricao() : "");
                MvtoSalaCirurgicaVO mvtoSalaCirurgicaVO = new MvtoSalaCirurgicaVO(mbcMvtoSalaCirurgica.getDtInicioMvto(),
                        mbcMvtoSalaCirurgica.getDtFimMvto(),
                        mbcMvtoSalaCirurgica.getNome(),
                        mbcMvtoSalaCirurgica.getMotivoInat(),
                        mbcMvtoSalaCirurgica.getSituacao(),
                        tipoSala,
                        mbcMvtoSalaCirurgica.getVisivelMonitor(),
                        criadoEm,
                        alteradoEm);
                mvtosSala.add(mvtoSalaCirurgicaVO);
            }
        }

        salaCirurgicaHistorico = blocoCirurgicoCadastroFacade.obterSalaCirurgicaBySalaCirurgicaId(seqp, unfSeq);
        setNomeSalaCirurgica(salaCirurgicaHistorico.getId().getSeqp() + " - " + salaCirurgicaHistorico.getNome());
        return mvtosSala;
    }

//    public void mostraMensagemTeste() {
//        this.apresentarMsgNegocio("LABEL_MOTIVO_INATIVACAO_HISTORICO_SALA_CIRURGICA");
//    }
	
	private AghUnidadesFuncionais carregarUnidadeFuncionalInicial() {
		String nomeMicrocomputador = null;
			
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			return blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e1) {
			LOG.error("Exceção capturada:", e1);
		}
		return null;
	}

	
	public String quebrarToolTipMotivoInativacao(String motivo, int intervalo) {		
		StringBuilder retorno = new StringBuilder();
		boolean flag = false;
	    int atualIndex = intervalo, auxIndex = 0;	    
	        
	    retorno.append(motivo);	   
	    if (motivo.length() > intervalo && intervalo > this.getBundle().getString("LABEL_MOTIVO_INATIVACAO_HISTORICO_SALA_CIRURGICA").length()) {
	    	atualIndex = intervalo - this.getBundle().getString("LABEL_MOTIVO_INATIVACAO_HISTORICO_SALA_CIRURGICA").length();
	        while (true) {
	        	if (retorno.charAt(atualIndex) != ' ') {
	        		atualIndex++;
	        		auxIndex++;
	        		flag = false;
	            } else {
	            	retorno.insert(atualIndex, "\n");
	                atualIndex += intervalo + 5;
	                
	                flag = true;
	            }

	            if (atualIndex >= retorno.length()) {
	            	break;
	            }
	        }
	        
	        if (flag == false) {
	        	atualIndex -= auxIndex;
	        	while (true) {
	        		retorno.insert(atualIndex, "\n");
	                atualIndex += intervalo + 5;
	        		
	        		if (atualIndex >= retorno.length()) {
	        			break;
	        		}
	        	}	        	
	        }
	    }
	        
	    return retorno.toString();
	}
		
	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public MbcSalaCirurgica getSalaCirurgica() {
		return this.salaCirurgica;
	}

	public void setSalasCirurgicas(List<MbcSalaCirurgica> salasCirurgicas) {
		this.salasCirurgicas = salasCirurgicas;
	}

	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}

	public List<MbcSalaCirurgica> getSalasCirurgicas() {
		return salasCirurgicas;
	}

	public boolean isExibirPesquisa() {
		return exibirPesquisa;
	}

	public void setExibirPesquisa(boolean exibirPesquisa) {
		this.exibirPesquisa = exibirPesquisa;
	}
	
	public DominioSimNao getMonitor() {
		return monitor;
	}

	public void setMonitor(DominioSimNao monitor) {
		this.monitor = monitor;
	}

	public boolean isCameFromEdit() {
		return cameFromEdit;
	}

	public void setCameFromEdit(boolean cameFromEdit) {
		this.cameFromEdit = cameFromEdit;
	}
	
	public List<MvtoSalaCirurgicaVO> getListaHistoricoSalaCirurgica() {
		return listaHistoricoSalaCirurgica;
	}

	public void setListaHistoricoSalaCirurgica(
			List<MvtoSalaCirurgicaVO> listaHistoricoSalaCirurgica) {
		this.listaHistoricoSalaCirurgica = listaHistoricoSalaCirurgica;
	}

	public String getNomeSalaCirurgica() {
		return nomeSalaCirurgica;
	}

	public void setNomeSalaCirurgica(String nomeSalaCirurgica) {
		this.nomeSalaCirurgica = nomeSalaCirurgica;
	}

	
	
	
}