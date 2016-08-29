package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.dao.PerfilDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosDAO;
import br.gov.mec.aghu.casca.dao.UsuarioDAO;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalServidorDAO;
import br.gov.mec.aghu.model.PtmArquivosAnexos;
import br.gov.mec.aghu.model.PtmArquivosAnexosJn;
import br.gov.mec.aghu.model.PtmNotaItemReceb;
import br.gov.mec.aghu.model.PtmNotaItemRecebId;
import br.gov.mec.aghu.model.PtmNotificacaoTecnica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecnicaAvaliacaoJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmArquivosAnexosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmArquivosAnexosJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmNotaItemRecebDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmServAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PtmArquivosAnexosRN extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 8978388519668133757L;
	
	public enum PtmArquivosAnexosONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ARQUIVO_OBRIGATORIO, MENSAGEM_TAMANHO_DO_ARQUIVO_ACIMA_PERMITIDO_10MB
	}
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	@Inject 
	private UsuarioDAO usuarioDAO;
	@Inject 
	private PerfilDAO perfilDAO;
	@Inject 
	private PerfisUsuariosDAO perfisUsuariosDAO;
	@Inject
	private PtmAreaTecnicaAvaliacaoJnDAO ptmAreaTecnicaAvaliacaoJnDAO;
	@Inject
	private FccCentroCustosDAO fccCentroCustosDAO;
	@Inject
	private PtmItemRecebProvisoriosDAO ptmItemRecebProvisoriosDAO; 
	@Inject
	private PtmServAreaTecAvaliacaoDAO ptmServAreaTecAvaliacaoDAO;
	@Inject
	private AghCaixaPostalDAO aghCaixaPostalDAO;
	@Inject
	private AghCaixaPostalServidorDAO aghCaixaPostalServidorDAO;
	@Inject
	private PtmNotaItemRecebDAO ptmNotaItemRecebDAO;
	@Inject
	private PtmArquivosAnexosJnDAO ptmArquivosAnexosJnDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@Inject
	private PtmAreaTecAvaliacaoDAO ptmAreaTecAvaliacaoDAO;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private PtmArquivosAnexosDAO ptmArquivosAnexosDAO;
	
	private RapServidores servidorLogado;
	

	/**
	 * #44713
	 * Inserir/Atualizar arquivos anexos
	 */
	public void salvar (PtmArquivosAnexos anexoForm, Long aaSeq, PtmNotificacaoTecnica notificacaoTecnica, Long irpSeq, boolean flagEditar) {
		
		this.servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		//ATUALIZAR
		if (aaSeq != null){			
			PtmArquivosAnexos arquivosAnexos = ptmArquivosAnexosDAO.obterPorChavePrimaria(aaSeq);
			arquivosAnexos.setDescricao(anexoForm.getDescricao());
			arquivosAnexos.setTipoDocumento(anexoForm.getTipoDocumento());
			arquivosAnexos.setTipoProcesso(anexoForm.getTipoProcesso());
			arquivosAnexos.setTipoDocumentoOutros(anexoForm.getTipoDocumentoOutros());
			arquivosAnexos.setArquivo(anexoForm.getArquivo());
			arquivosAnexos.setAnexo(anexoForm.getAnexo());
			
			arquivosAnexos.setAlteradoEm(new Date());
			arquivosAnexos.setServidorAlteracao(servidorLogado);
			
			ptmArquivosAnexosDAO.atualizar(arquivosAnexos);
			atualizarJournal(arquivosAnexos);
		}
		//INSERIR
		else if (anexoForm != null) {
			ptmArquivosAnexosDAO.persistir(anexoForm);
			ptmArquivosAnexosDAO.flush();
			persistirJournal(anexoForm);
			
			if(anexoForm.getPtmNotificacaoTecnica()!= null){
				
				irpSeq = patrimonioFacade.obterIrpSeqNotificacoesTecnica(notificacaoTecnica.getSeq());
				
				if((irpSeq!=null) && (!flagEditar)){
					persistirNotaItemReceb(notificacaoTecnica.getSeq(), irpSeq);
				}
			}	
		}
	}
	
	public void persistirNotaItemReceb(Long aaSeq, Long irpSeq) {
		
		PtmNotaItemReceb notaItemReceb = new PtmNotaItemReceb();
		PtmNotaItemReceb notaItemRecebOriginal;
		
		 PtmNotaItemRecebId id = new PtmNotaItemRecebId();
		 id.setIrpSeq(irpSeq);
		 id.setNotSeq(aaSeq);

		 notaItemReceb.setId(id);
		 
		 notaItemReceb.setServidor(servidorLogado);
		 notaItemReceb.setCriadoEm(new Date());
		 
		 notaItemRecebOriginal = ptmNotaItemRecebDAO.obterPorChavePrimaria(id);
		 if(notaItemRecebOriginal == null){
			 ptmNotaItemRecebDAO.persistir(notaItemReceb);
		 }
	}
	
	/**
	 * Retorna o conteudo do parametro tamanhoMaximoPermitido para arquivos anexos
	 * @return BigDecimal Tamanho máximo de arquivo em bytes
	 * @throws ApplicationBusinessException Se não encontrar o parametro
	 */
	public BigDecimal tamanhoMaximoPermitido() throws ApplicationBusinessException{
		return this.getParametroFacade().buscarValorNumerico(AghuParametrosEnum.P_TAMANHO_MAX_ARQUIVO);
	}
		
	public void validarArquivoAnexo(PtmArquivosAnexos arquivoAnexo) throws ApplicationBusinessException{		
		if (arquivoAnexo.getAnexo() == null || arquivoAnexo.getAnexo().length == 0){
			throw new ApplicationBusinessException(PtmArquivosAnexosONExceptionCode.MENSAGEM_TAMANHO_DO_ARQUIVO_ACIMA_PERMITIDO_10MB);
		}else{
			BigDecimal tamanhoArquivo = new BigDecimal(arquivoAnexo.getAnexo().length);
			BigDecimal tamanhoMaximoArquivo =  tamanhoMaximoPermitido();
			
			int testaTamanho = tamanhoArquivo.compareTo(tamanhoMaximoArquivo);
			
			if (testaTamanho > 0){
				throw new ApplicationBusinessException(PtmArquivosAnexosONExceptionCode.MENSAGEM_ARQUIVO_OBRIGATORIO);
			}
		}		
	}
	
	public void atualizarJournal(PtmArquivosAnexos aa){
		PtmArquivosAnexosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, PtmArquivosAnexosJn.class, aa.getServidor() != null ? aa.getServidor().getUsuario() : null);
		inserirAtributosJournal(jn,aa);
		ptmArquivosAnexosJnDAO.persistir(jn); 
	}

	public void persistirJournal(PtmArquivosAnexos aa){
		PtmArquivosAnexosJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.INS, PtmArquivosAnexosJn.class, aa.getServidor() != null ? aa.getServidor().getUsuario() : null);
		inserirAtributosJournal(jn,aa);
		ptmArquivosAnexosJnDAO.persistir(jn); 
	}
	
	private void inserirAtributosJournal(PtmArquivosAnexosJn jn,PtmArquivosAnexos aa) {
		jn.setAlteradoEm(aa.getAlteradoEm());
		jn.setAnexo(aa.getAnexo());
		jn.setArquivo(aa.getArquivo());
		jn.setCriadoEm(aa.getCriadoEm());
		jn.setDescricao(aa.getDescricao());
		jn.setSeq(aa.getSeq());
		
		if(aa.getAceiteTecnico()!= null){
			jn.setAceiteTecnico(aa.getAceiteTecnico());
		}
		
		if(aa.getServidor()!=null){
			jn.setSerMatricula(aa.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(aa.getServidor().getId().getVinCodigo());
			
		}
		
		if(aa.getServidorAlteracao()!=null){
			jn.setSerMatriculaAlteracao(aa.getServidorAlteracao().getId().getMatricula());
			jn.setSerVinCodigoAlteracao(aa.getServidorAlteracao().getId().getVinCodigo());
		}
		
		jn.setTipoDocumento(aa.getTipoDocumento());
		jn.setTipoProcesso(aa.getTipoProcesso());
		jn.setVersion(aa.getVersion());		
		if(aa.getPtmBemPermanentes()!=null){
			jn.setBpeSeq(aa.getPtmBemPermanentes().getSeq());
		}
		if(aa.getPtmItemRecebProvisorios()!=null){
			jn.setIrpSeq(aa.getPtmItemRecebProvisorios().getSeq());
		}
		if(aa.getPtmNotificacaoTecnica()!=null){
			jn.setNotSeq(aa.getPtmNotificacaoTecnica().getSeq());
		}
		if(aa.getPtmProcessos()!=null){
			jn.setProSeq(aa.getPtmProcessos().getSeq());
		}
	}
	
	public UsuarioDAO getUsuarioDAO() {
		return usuarioDAO;
	}

	public PtmAreaTecAvaliacaoDAO getPtmAreaTecAvaliacaoDAO() {
		return ptmAreaTecAvaliacaoDAO;
	}
	
	public RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}

	public PerfilDAO getPerfilDAO() {
		return perfilDAO;
	}

	public PerfisUsuariosDAO getPerfisUsuariosDAO() {
		return perfisUsuariosDAO;
	}
	
	public PtmAreaTecnicaAvaliacaoJnDAO getPtmAreaTecnicaAvaliacaoJnDAO(){
		return this.ptmAreaTecnicaAvaliacaoJnDAO;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public FccCentroCustosDAO getFccCentroCustosDAO() {
		return fccCentroCustosDAO;
	}
	
	public PtmItemRecebProvisoriosDAO getPtmItemRecebProvisoriosDAO() {
		return ptmItemRecebProvisoriosDAO;
	}
	
	public PtmServAreaTecAvaliacaoDAO getPtmServAreaTecAvaliacaoDAO() {
		return ptmServAreaTecAvaliacaoDAO;
	}
	
	public AghCaixaPostalDAO getAghCaixaPostalDAO() {
		return aghCaixaPostalDAO;
	}

	public AghCaixaPostalServidorDAO getAghCaixaPostalServidorDAO() {
		return aghCaixaPostalServidorDAO;
	}
	
	public IPermissionService getPermissionService() {
		return permissionService;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}
	
	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
