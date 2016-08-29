package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamItemReceitCuidadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemReceituarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRecCuidPreferidoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituarioCuidadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.vo.MamRecCuidPreferidoVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamItemReceitCuidado;
import br.gov.mec.aghu.model.MamItemReceitCuidadoId;
import br.gov.mec.aghu.model.MamRecCuidPreferido;
import br.gov.mec.aghu.model.MamReceituarioCuidado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VMamDiferCuidServidores;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class CuidadoPacienteRN extends BaseBusiness {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6776025124755395505L;

	private static final Log LOG = LogFactory.getLog(CuidadoPacienteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AipPacientesDAO pacienteDAO;
	
	
	@Inject
	private MamReceituariosDAO mamReceituariosDAO;
	
	@Inject
	private MamReceituarioCuidadoDAO mamReceituarioCuidadoDAO;
	
	@Inject
	private MamItemReceituarioDAO mamItemReceituarioDAO;
	
	@Inject
	private MamItemReceitCuidadoDAO mamItemReceitCuidadoDAO;
	
	@Inject
	private MamRecCuidPreferidoDAO mamRecCuidPreferidoDAO;
	
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	
	/**
	 * #11960 RF02 
	 * @param numeroConsulta
	 */
	public MamReceituarioCuidado verificaRequisitosReceituarioCuidado(AacConsultas consulta){
	//MamReceituarioCuidado itemAntigo = mamReceituarioCuidadoDAO.mamReceituarioCuidadoPorNumeroConsulta(consulta.getNumero(),DominioIndPendenteAmbulatorio.V);
		MamReceituarioCuidado ultimoReceituarioCuidado = mamReceituarioCuidadoDAO.obterUltimoMamReceituarioCuidadoPorNumeroConsultaSemPendente(consulta.getNumero());
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MamReceituarioCuidado item = new MamReceituarioCuidado();
		
		if(ultimoReceituarioCuidado!=null ){
			MamReceituarioCuidado receituarioCuidadoAntigo  = mamReceituarioCuidadoDAO.mamReceituarioCuidadoPorNumeroConsulta(consulta.getNumero(),DominioIndPendenteAmbulatorio.V);
			item.setDthrCriacao(new Date());
			item.setPaciente(consulta.getPaciente());
			item.setConsulta(consulta);
			item.setServidor(servidorLogado);
			item.setPendente(DominioIndPendenteAmbulatorio.R);
			item.setImpresso(Boolean.FALSE);
			if(receituarioCuidadoAntigo!=null){
				item.setNroVias(Byte.valueOf("1"));
				item.setVersion(0);
				mamReceituarioCuidadoDAO.persistir(item);
				ajustarReceituarioAnterior(receituarioCuidadoAntigo, servidorLogado);
			}
			if(receituarioCuidadoAntigo != null){
				copiarItensReceiturarioAnterior(item,receituarioCuidadoAntigo);
			}
		}
		return item;
	}
	
	private void copiarItensReceiturarioAnterior(MamReceituarioCuidado itemPersistido,MamReceituarioCuidado itemAntigo){
		//Buscar itens do antigo, copiar para o novo
		List<MamItemReceitCuidado> listaIntensReceituarioCuidadoAntigo = mamItemReceitCuidadoDAO.listarMamItemReceitCuidadoPorReceituarioCuidado(itemAntigo.getSeq());
		
		if(listaIntensReceituarioCuidadoAntigo != null && !listaIntensReceituarioCuidadoAntigo.isEmpty()){
			for(MamItemReceitCuidado itemVelho:listaIntensReceituarioCuidadoAntigo){
				MamItemReceitCuidado itemNovo = new MamItemReceitCuidado();
				itemNovo.setDescricao(itemVelho.getDescricao());
				itemNovo.setId(new MamItemReceitCuidadoId(itemPersistido.getSeq(), itemVelho.getId().getSeqp()));
				mamItemReceitCuidadoDAO.persistir(itemNovo);
			}
		}
	}
	
	/**
	 * RF03 e RF01
	 * @param receituarioAtual (Ultimo registro da lista C2)
	 * @param receituarioAnterior
	 * @param itemNovo
	 */
	public void adicionarEditarMamItemReceitCuidado(MamReceituarioCuidado receituarioAtual,MamReceituarioCuidado receituarioAnterior,MamItemReceitCuidado itemNovo,AacConsultas consulta){
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (receituarioAtual.getSeq() != null) {//RF03 03receituarioAtual
			MamReceituarioCuidado receituarioOriginal = mamReceituarioCuidadoDAO.obterPorChavePrimaria(receituarioAtual.getSeq());
			receituarioOriginal.setPendente(DominioIndPendenteAmbulatorio.P);
			receituarioOriginal.setNroVias(receituarioAtual.getNroVias());
			mamReceituarioCuidadoDAO.atualizar(receituarioOriginal);
			if (itemNovo.getId() == null) {// incluindo novo item
				itemNovo.setId(new MamItemReceitCuidadoId(receituarioAtual.getSeq(), mamItemReceitCuidadoDAO.buscarSeqp(receituarioAtual.getSeq())));
				mamItemReceitCuidadoDAO.persistir(itemNovo);
			} else {// Caso edição
				MamItemReceitCuidado itemNovoOriginal = mamItemReceitCuidadoDAO.obterPorChavePrimaria(itemNovo.getId());
				itemNovoOriginal.setDescricao(itemNovo.getDescricao());
				mamItemReceitCuidadoDAO.atualizar(itemNovoOriginal);
			}
			if (receituarioAnterior != null) {
				ajustarReceituarioAnterior(receituarioAnterior,
						servidorLogado);
			}
		}
		else{//RF01
			gravarPrimeiroMamReceituarioConsulta(receituarioAtual,consulta,servidorLogado);
			if (itemNovo.getId() == null){
				itemNovo.setId(new MamItemReceitCuidadoId(receituarioAtual.getSeq(), mamItemReceitCuidadoDAO.buscarSeqp(receituarioAtual.getSeq())));
				mamItemReceitCuidadoDAO.persistir(itemNovo);
			}
		}
	}
	
	public void procedimentosReceituarioCuidadoFinalizarAtendimento(Integer numeroConsulta,MamReceituarioCuidado receituarioAnterior){
		
		//MamReceituarioCuidado receituarioAnterior = mamReceituarioCuidadoDAO.mamReceituarioCuidadoPorNumeroConsulta(numeroConsulta,DominioIndPendenteAmbulatorio.V);
		MamReceituarioCuidado receituarioAtual = mamReceituarioCuidadoDAO.obterUltimoMamReceituarioCuidadoPorNumeroConsulta(numeroConsulta);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		//receituarioAtual= mamReceituarioCuidadoDAO.obterPorChavePrimaria(receituarioAtual.getSeq());
		if (receituarioAtual != null && receituarioAtual.getSeq() != null) {
			receituarioAtual.setDthrValida(new Date());
			receituarioAtual.setServidorValida(servidorLogado);
			receituarioAtual.setPendente(DominioIndPendenteAmbulatorio.V);
			mamReceituarioCuidadoDAO.atualizar(receituarioAtual);
		}
		if (receituarioAnterior != null && receituarioAnterior.getSeq() != null) {
			receituarioAnterior= mamReceituarioCuidadoDAO.obterPorChavePrimaria(receituarioAnterior.getSeq());
			receituarioAnterior.setPendente(DominioIndPendenteAmbulatorio.V);
			receituarioAnterior.setDthrValidaMvto(new Date());
			mamReceituarioCuidadoDAO.atualizar(receituarioAnterior);
		}
		
	}
	
	public void procedimentosReceituarioCuidadoCancelarAtendimento(Integer numeroConsulta,MamReceituarioCuidado anterior){
		List<Long> listaSeq = mamReceituarioCuidadoDAO.listarMamReceituarioCuidadoPorNumeroConsulta(numeroConsulta);
		if(listaSeq != null && !listaSeq.isEmpty()){
			for(Long receituarioSeq:listaSeq){
				removerItensReceituarioCuidado(receituarioSeq);
				mamReceituarioCuidadoDAO.remover(mamReceituarioCuidadoDAO.obterPorChavePrimaria(receituarioSeq));
			}
		}

		if(anterior!=null && anterior.getSeq() != null){
			MamReceituarioCuidado receituarioAnterior = mamReceituarioCuidadoDAO.obterPorChavePrimaria(anterior.getSeq());
			receituarioAnterior.setDthrMvto(null);
			receituarioAnterior.setServidorMvto(null);
			mamReceituarioCuidadoDAO.atualizar(receituarioAnterior);
		}
	}
	
	public void removerItensReceituarioCuidado(Long receituarioSeq){
		
		List<MamItemReceitCuidado> listaIntensReceituarioCuidadoAntigo = mamItemReceitCuidadoDAO.listarMamItemReceitCuidadoPorReceituarioCuidado(receituarioSeq);
				if(listaIntensReceituarioCuidadoAntigo!=null && !listaIntensReceituarioCuidadoAntigo.isEmpty()){
					mamItemReceitCuidadoDAO.removerItemRelacionadoPorReceituario(receituarioSeq);
				}
	}

	private void ajustarReceituarioAnterior(
			MamReceituarioCuidado receituarioAnterior,
			RapServidores servidorLogado) {
		receituarioAnterior = mamReceituarioCuidadoDAO.obterPorChavePrimaria(receituarioAnterior.getSeq());
		receituarioAnterior.setDthrMvto(new Date());
		receituarioAnterior
				.setPendente(DominioIndPendenteAmbulatorio.A);
		receituarioAnterior.setServidorMvto(servidorLogado);
		mamReceituarioCuidadoDAO.atualizar(receituarioAnterior);
	}

	private void gravarPrimeiroMamReceituarioConsulta(
		MamReceituarioCuidado receituarioAtual,AacConsultas consulta,RapServidores servidorLogado) {
		receituarioAtual.setPendente(DominioIndPendenteAmbulatorio.P);
		receituarioAtual.setImpresso(Boolean.FALSE);
		receituarioAtual.setDthrCriacao(new Date());
		receituarioAtual.setConsulta(consulta);
		receituarioAtual.setServidor(servidorLogado);
		receituarioAtual.setPaciente(consulta.getPaciente());
		
		mamReceituarioCuidadoDAO.persistir(receituarioAtual);
	}
	
	
	
	private void gravarMamReceituarioConsultaCoiandoPreferidos(
			MamReceituarioCuidado receituarioAtual,AacConsultas consulta,RapServidores servidorLogado) {
			receituarioAtual.setPendente(DominioIndPendenteAmbulatorio.P);
			receituarioAtual.setImpresso(Boolean.FALSE);
			receituarioAtual.setDthrCriacao(new Date());
			receituarioAtual.setDthrValida(new Date());
			receituarioAtual.setConsulta(consulta);
			receituarioAtual.setServidor(servidorLogado);
			receituarioAtual.setPaciente(consulta.getPaciente());
			receituarioAtual.setNroVias(Byte.valueOf("1"));
			receituarioAtual.setVersion(0);
			mamReceituarioCuidadoDAO.persistir(receituarioAtual);
		}
	
	public void selecionarCuidadosEntrePreferidosUsuario(List<MamRecCuidPreferidoVO> listaMamRecCuidPreferido,MamReceituarioCuidado receituarioAtual,AacConsultas consulta){
		
		
		if (receituarioAtual == null ||receituarioAtual.getSeq()==null) {
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			gravarMamReceituarioConsultaCoiandoPreferidos(receituarioAtual,consulta,servidorLogado);
		}
		
		for(MamRecCuidPreferidoVO itemPreferido:listaMamRecCuidPreferido){
			if(itemPreferido.isSelecionado()){
				MamItemReceitCuidado itemNovo = new MamItemReceitCuidado();
				itemNovo.setDescricao(itemPreferido.getDescricao());
				itemNovo.setId(new MamItemReceitCuidadoId(receituarioAtual.getSeq(), mamItemReceitCuidadoDAO.buscarSeqp(receituarioAtual.getSeq())));
				mamItemReceitCuidadoDAO.persistir(itemNovo);
			}
		}
		
	}
	
	public void copiaCuidadoPreferidosOutroUsuario(VMamDiferCuidServidores servidor,RapServidores servidorLogado){
				
		List<MamRecCuidPreferidoVO> listaPreferidos = mamRecCuidPreferidoDAO.listarCuidadosPreferidos(
				rapServidoresDAO.obterPorChavePrimaria( new RapServidoresId(servidor.getMatricula(), servidor.getVinCodigo())) ,true);
		
		
		for(MamRecCuidPreferidoVO itemLista:listaPreferidos){
			MamRecCuidPreferido itemNovo = new MamRecCuidPreferido();
			itemNovo.setDescricao(itemLista.getDescricao());
			itemNovo.setRapServidores(servidorLogado);
			itemNovo.setCriadoEm(new Date());
			itemNovo.setIndSituacao("A");	
			mamRecCuidPreferidoDAO.persistir(itemNovo);
		}
	}
	
	public void excluirmamItemReceitCuidado(MamItemReceitCuidado item){
		mamItemReceitCuidadoDAO.remover(mamItemReceitCuidadoDAO.obterPorChavePrimaria(item.getId()));
	}
	
	public void setMamReceituariosDAO(MamReceituariosDAO mamReceituariosDAO) {
		this.mamReceituariosDAO = mamReceituariosDAO;
	}

	public MamReceituariosDAO getMamReceituariosDAO() {
		return mamReceituariosDAO;
	}

	public MamItemReceituarioDAO getMamItemReceituarioDAO() {
		return mamItemReceituarioDAO;
	}


	public void setMamItemReceituarioDAO(MamItemReceituarioDAO mamItemReceituarioDAO) {
		this.mamItemReceituarioDAO = mamItemReceituarioDAO;
	}

	public AipPacientesDAO getPacienteDAO() {
		return pacienteDAO;
	}

	public void setPacienteDAO(AipPacientesDAO pacienteDAO) {
		this.pacienteDAO = pacienteDAO;
	}
	
	
}
