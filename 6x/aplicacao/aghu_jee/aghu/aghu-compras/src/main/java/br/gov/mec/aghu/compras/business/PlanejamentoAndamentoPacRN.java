package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAndamentoProcessoCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoEtapaModPacDAO;
import br.gov.mec.aghu.compras.dao.ScoEtapaPacDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLocalizacaoProcessoDAO;
import br.gov.mec.aghu.compras.dao.ScoLogEtapaPacDAO;
import br.gov.mec.aghu.compras.vo.EtapaPACVO;
import br.gov.mec.aghu.compras.vo.EtapasRelacionadasPacVO;
import br.gov.mec.aghu.compras.vo.HistoricoLogEtapaPacVO;
import br.gov.mec.aghu.compras.vo.LocalPACVO;
import br.gov.mec.aghu.compras.vo.ModPacSolicCompraServicoVO;
import br.gov.mec.aghu.dominio.DominioObjetoDoPac;
import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoEtapaModPac;
import br.gov.mec.aghu.model.ScoEtapaPac;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoLogEtapaPac;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PlanejamentoAndamentoPacRN extends BaseBusiness{


	private static final long serialVersionUID = -5220563566996169146L;
	
	public enum PlanejamentoAndamentoPacRNExceptionCode implements BusinessExceptionCode {
		 ETAPA_EXISTENTE;
	}

	private static final Log LOG = LogFactory.getLog(PlanejamentoAndamentoPacRN.class);
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@Inject
	private ScoAndamentoProcessoCompraDAO scoAndamentoProcessoCompraDAO;
	
	@Inject
	private ScoEtapaPacDAO scoEtapaPacDAO;
	
	@Inject
	private ScoEtapaModPacDAO scoEtapaModPacDAO;
	
	@Inject
	private ScoLocalizacaoProcessoDAO scoLocalizacaoProcessoDAO;
	
	@Inject
	private ScoLogEtapaPacDAO scoLogEtapaPacDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}
	
	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	protected ScoAndamentoProcessoCompraDAO getScoAndamentoProcessoCompraDAO(){
		return scoAndamentoProcessoCompraDAO;
	}
	
	protected ScoEtapaPacDAO getScoEtapaPacDAO() {
		return scoEtapaPacDAO;
	}
	
	protected ScoEtapaModPacDAO getScoEtapaModPacDAO() {
		return scoEtapaModPacDAO;
	}
	
	protected ScoLocalizacaoProcessoDAO getScoLocalizacaoProcessoDAO() {
		return scoLocalizacaoProcessoDAO;
	}
	
	protected ScoLogEtapaPacDAO getScoLogEtapaPacDAO() {
		return scoLogEtapaPacDAO;
	}
	
	public ModPacSolicCompraServicoVO obterModaldiadePacSocilitacao(Integer numeroLicitacao, String mlcCodigo) {
		
		ModPacSolicCompraServicoVO vo = new ModPacSolicCompraServicoVO();
		
		this.obterModalidadeObjeto(numeroLicitacao, vo);
		this.obterLocalidadeAtual(numeroLicitacao, vo);
		this.obterTempoPrevisto(numeroLicitacao, vo, mlcCodigo);
		
		return vo;
	}

	private void obterModalidadeObjeto(Integer numeroLicitacao, ModPacSolicCompraServicoVO vo) {
 		
		// C1 - #22068
		List<Object[]> listaVO = this.getScoItemLicitacaoDAO().obterModaldiadePacSocilitacao(numeroLicitacao);
		
		for (Object[] obj : listaVO) {
 			vo.setDescricaoModalidade(obj[0].toString());
 			vo.setCodigoModalidade(obj[3].toString());
			vo.setDescricaoObjeto(this.verificarObjetoPac(obj));
 		}
	}

	private void obterLocalidadeAtual(Integer numeroLicitacao, ModPacSolicCompraServicoVO vo) {
		
		// C2 - #22068
		Object objLocalidade = this.getScoAndamentoProcessoCompraDAO().obterLocalidadeAtual(numeroLicitacao);
		
		if(objLocalidade != null){
			vo.setLocalidadeAtual(objLocalidade.toString());
		}
		
	}
	
	// RN01 - #22068
	private String verificarObjetoPac(Object[] obj) {
		
		if(obj[1] != null && obj[2] == null){
			if(Integer.parseInt(obj[1].toString()) != 0){
				return DominioObjetoDoPac.M.getDescricao();
			}
		} else if(obj[2] != null && obj[1] == null){
			if(Integer.parseInt(obj[2].toString()) != 0){
				return DominioObjetoDoPac.S.getDescricao();
			}
		} else if(obj[2] != null && obj[1] != null){
			return DominioObjetoDoPac.M.getDescricao().concat(" e ").concat(DominioObjetoDoPac.S.getDescricao());
		}
		return null;
	}
	
	// RN03 - #22068
	private void obterTempoPrevisto(Integer numeroLicitacao, ModPacSolicCompraServicoVO vo, String mlcCodigo) {
		
		Short tempo = null;
		if(this.obterEtapaPacPorLicitacao(numeroLicitacao) != null){
			
			// C4 - #22068
			tempo = this.getScoEtapaPacDAO().obterTempoPrevisto(numeroLicitacao);
			vo.setTempoPrevisto(tempo != null ? tempo.toString().concat(" dias.") : null);
		} else {
			
			Boolean materialServico = this.verificarDescricaoObj(vo.getDescricaoObjeto());

			// C5 - #22068
			tempo = this.getScoEtapaModPacDAO().obterNumeroDiasPac(mlcCodigo, materialServico);
			vo.setTempoPrevisto(tempo != null ? tempo.toString().concat(" dias.") : null);
		}
		
	}
	
	private boolean verificarDescricaoObj(String descricaoObjeto){
		Boolean materialServico = Boolean.FALSE;
		if(descricaoObjeto.equals(DominioObjetoDoPac.M.getDescricao()) 
				|| descricaoObjeto.equals(DominioObjetoDoPac.S.getDescricao())){
			materialServico = Boolean.TRUE;
		}
		
		return materialServico;
	}
	
	// C3 - RN02 - #22068
	public List<ScoEtapaPac> obterEtapaPacPorLicitacao(Integer numeroLicitacao){
		return this.getScoEtapaPacDAO().obterEtapaPacPorLicitacao(numeroLicitacao);
	}
	
	
	public List<EtapasRelacionadasPacVO> acompanharHistorico(Integer numeroLicitacao, String descricaoObjeto, String codigoMaterial){
		
		Boolean materialServico = this.verificarDescricaoObj(descricaoObjeto);
		
		// I1 - #22068
		this.inserirEtapaPac(numeroLicitacao, codigoMaterial, materialServico);
		
		List<EtapasRelacionadasPacVO> listaVO = this.obterEtapasRelacionadasPAC(numeroLicitacao, null, null, null, null, null);

		return listaVO;
	}
	
	// C8 - #22068
	private List<EtapasRelacionadasPacVO> obterEtapasRelacionadasPAC(Integer numeroLicitacao, LocalPACVO localPACVO, 
																		RapServidoresId idServidor, DominioSituacaoEtapaPac situacao,
																		EtapaPACVO etapaVO, Date dataApontamento){
		
		List<EtapasRelacionadasPacVO> listaVO = new ArrayList<EtapasRelacionadasPacVO>();
		List<ScoEtapaPac> resultQuery = this.getScoEtapaPacDAO().obterEtapasRelacionadasPAC(numeroLicitacao, localPACVO, 
																							idServidor, situacao, 
																							etapaVO, dataApontamento);
		
		for (ScoEtapaPac etapa : resultQuery) {
			
			EtapasRelacionadasPacVO vo = new EtapasRelacionadasPacVO();
			
			vo.setDescricaoLocProcesso(etapa.getLocalizacaoProcesso().getDescricao());
			vo.setDescricaoEtapa(etapa.getDescricaoEtapa());
			vo.setSituacao(etapa.getSituacao());
			vo.setApontamentoUsuario(etapa.getApontamentoUsuario());
			vo.setNome(etapa.getServidor() != null ? etapa.getServidor().getPessoaFisica().getNome() : null);
			vo.setTempoPrevisto(etapa.getTempoPrevisto() != null ? etapa.getTempoPrevisto() : 0);
			vo.setDataApontamento(etapa.getDataApontamento());
			vo.setCodigoEtapa(etapa.getCodigo());
			
			listaVO.add(vo);
		}
		
		return listaVO;
	}
	
	// RN04 - #22068
	private void inserirEtapaPac(Integer numeroLicitacao, String codigoMaterial, Boolean materialServico){
		
		List<ScoEtapaModPac> listaEtapaModPac = this.getScoEtapaModPacDAO().obterDadosEtapaModPac(codigoMaterial, materialServico);
		
		for(ScoEtapaModPac etapaModPac : listaEtapaModPac){
			
			ScoEtapaPac etapaPac = new ScoEtapaPac();
			
			ScoLicitacao licitacao = this.getScoLicitacaoDAO().obterPorChavePrimaria(numeroLicitacao);
			etapaPac.setLicitacao(licitacao);
			
			ScoLocalizacaoProcesso localizacaoProcesso = this.getScoLocalizacaoProcessoDAO().obterPorChavePrimaria(etapaModPac.getLocalizacaoProcesso().getCodigo());
			etapaPac.setLocalizacaoProcesso(localizacaoProcesso);
			
			etapaPac.setDescricaoEtapa(etapaModPac.getDescricao());
			etapaPac.setSituacao(DominioSituacaoEtapaPac.PD);

			etapaPac.setTempoPrevisto(etapaModPac.getNumeroDias());
			etapaPac.setVersion(etapaModPac.getVersion());
			
			this.getScoEtapaPacDAO().persistir(etapaPac);
			this.getScoEtapaPacDAO().flush();
		}
	}

	public List<LocalPACVO> pesquisarLocaisPac(Integer numeroLicitacao, String mdlCodigo, String objeto) {

		List<LocalPACVO> listaPacVO = new ArrayList<LocalPACVO>();
		
		List<ScoEtapaPac> etapa = this.obterEtapaPacPorLicitacao(numeroLicitacao);
		
		
		if(etapa != null){
			this.pesquisarLocaisEtapa(listaPacVO, numeroLicitacao);
		} else {
			this.pesquisarLocaisModalidade(objeto, mdlCodigo, listaPacVO);
		}
		
		return listaPacVO;
	}
	
	private void pesquisarLocaisEtapa(List<LocalPACVO> listaPacVO, Integer numeroLicitacao){
		
		List<ScoEtapaPac> etapaPac = this.getScoEtapaPacDAO().obterLocaisPrevistosPAC(numeroLicitacao);
		
		for (ScoEtapaPac etp : etapaPac) {
			LocalPACVO etapaVO = new LocalPACVO();
			etapaVO.setCodigo(etp.getLocalizacaoProcesso().getCodigo());
			etapaVO.setDescricao(etp.getLocalizacaoProcesso().getDescricao());
			
			listaPacVO.add(etapaVO);
		}
	}
	
	private void pesquisarLocaisModalidade(String objeto, String mdlCodigo, List<LocalPACVO> listaPacVO){
		
		Boolean material = null;
		if(objeto.equals(DominioObjetoDoPac.M.getDescricao())){
			material = Boolean.TRUE;
		} else if(objeto.equals(DominioObjetoDoPac.S.getDescricao())){
			material = Boolean.FALSE;
		}
		
		List<ScoEtapaModPac> etapaModPac = this.getScoEtapaModPacDAO().obterLocaisPrevistosModPac(mdlCodigo, material);
		
		for (ScoEtapaModPac emp : etapaModPac) {
			LocalPACVO modVO = new LocalPACVO();
			modVO.setCodigo(emp.getLocalizacaoProcesso().getCodigo());
			modVO.setDescricao(emp.getLocalizacaoProcesso().getDescricao());
			
			listaPacVO.add(modVO);
		}
	}

	// RN05 - #22068
	public List<EtapaPACVO> pesquisaEtapasPac(Object objEtapa, Integer numeroLicitacao, 
												LocalPACVO localPACVO, String descricaoObjeto,
												String codigoModalidade) {

		List<EtapaPACVO> listaEtapa = new ArrayList<EtapaPACVO>();
		List<ScoEtapaPac> etapa = this.obterEtapaPacPorLicitacao(numeroLicitacao);
		
		if(etapa != null){
			this.montarSgEtapa(objEtapa, listaEtapa, numeroLicitacao, localPACVO);
		} else {
			this.montarSgEtapaMod(objEtapa, listaEtapa, codigoModalidade, descricaoObjeto, localPACVO);
		}
		
		return listaEtapa;
	}

	private void montarSgEtapa(Object objEtapa, List<EtapaPACVO> listaEtapa, Integer numeroLicitacao, LocalPACVO localPACVO) {

		List<Object[]> etapaPac = this.getScoEtapaPacDAO().obterEtapaPac(objEtapa, numeroLicitacao, localPACVO);
		
		for (Object[] etp : etapaPac) {
			EtapaPACVO vo = new EtapaPACVO();
			vo.setCodigo(Integer.parseInt(etp[1].toString()));
			vo.setDescricao(etp[0].toString());
			
			listaEtapa.add(vo);
		}
		
	}

	private void montarSgEtapaMod(Object objEtapa, List<EtapaPACVO> listaEtapa, String codigoModalidade, String descricaoObjeto, LocalPACVO localPACVO) {

		Boolean material = null;
		if(descricaoObjeto.equals(DominioObjetoDoPac.M.getDescricao())){
			material = Boolean.TRUE;
		} else if(descricaoObjeto.equals(DominioObjetoDoPac.S.getDescricao())){
			material = Boolean.FALSE;
		}
		
		List<ScoEtapaModPac> etapaModPac = this.getScoEtapaModPacDAO().obterEtapaModPac(objEtapa, codigoModalidade, localPACVO, material);
		
		for (ScoEtapaModPac emp : etapaModPac) {
			
			EtapaPACVO vo = new EtapaPACVO();
			vo.setCodigo(emp.getCodigo());
			vo.setDescricao(emp.getDescricao());
			
			listaEtapa.add(vo);
		}
	}

	// RN06 - #22068
	public List<EtapasRelacionadasPacVO> pesquisarEtapas(Integer numeroLicitacao, ModPacSolicCompraServicoVO modPacSolicCompraServicoVO,
															DominioSituacaoEtapaPac situacaoEtapa, LocalPACVO localPACVO,
															RapServidoresId idServidor, EtapaPACVO etapaVO, Date dataPac) {

		List<EtapasRelacionadasPacVO> listaVO = new ArrayList<EtapasRelacionadasPacVO>();
		
		List<ScoEtapaPac> etapacPac = this.obterEtapaPacPorLicitacao(numeroLicitacao);
		
		if(etapacPac != null){
			this.pesquisarEtapasPacComHistorico(numeroLicitacao, modPacSolicCompraServicoVO, idServidor, 
													dataPac, etapaVO, situacaoEtapa, localPACVO, listaVO);
		} else {
			this.pesquisarEtapasPacSemHistorico(modPacSolicCompraServicoVO, localPACVO, etapaVO, listaVO);
		}
		
		
		return listaVO;
	}

	private void pesquisarEtapasPacComHistorico(Integer numeroLicitacao, ModPacSolicCompraServicoVO modPacSolicCompraServicoVO,
													RapServidoresId idServidor, Date dataPac, EtapaPACVO etapaVO,
													DominioSituacaoEtapaPac situacaoEtapa, LocalPACVO localPACVO,
													List<EtapasRelacionadasPacVO> listaVO) {
		
		Date dataApontamento = null;
		if(dataPac != null){
			dataApontamento = DateUtil.truncaData(dataPac);
		}
		List<ScoEtapaPac> etapasRelacionadas = this.getScoEtapaPacDAO().obterEtapasRelacionadasPAC(numeroLicitacao, 
																									localPACVO,
																									idServidor, 
																									situacaoEtapa, 
																									etapaVO, 
																									dataApontamento);
		
		for (ScoEtapaPac etapa : etapasRelacionadas) {
			EtapasRelacionadasPacVO vo = new EtapasRelacionadasPacVO();
			
			vo.setDescricaoLocProcesso(etapa.getLocalizacaoProcesso().getDescricao());
			vo.setDescricaoEtapa(etapa.getDescricaoEtapa());
			vo.setSituacao(etapa.getSituacao());
			vo.setApontamentoUsuario(etapa.getApontamentoUsuario());
			vo.setNome(etapa.getServidor() != null ? etapa.getServidor().getPessoaFisica().getNome() : null);
			vo.setTempoPrevisto(etapa.getTempoPrevisto() != null ? etapa.getTempoPrevisto() : 0);
			vo.setDataApontamento(etapa.getDataApontamento());
			vo.setCodigoEtapa(etapa.getCodigo());
			
			listaVO.add(vo);
		}
		
	}
	
	private void pesquisarEtapasPacSemHistorico(ModPacSolicCompraServicoVO modPacSolicCompraServicoVO,
													LocalPACVO localPACVO, EtapaPACVO etapaVO, List<EtapasRelacionadasPacVO> listaVO) {

		Boolean material = null;
		if(modPacSolicCompraServicoVO.getDescricaoObjeto().equals(DominioObjetoDoPac.M.getDescricao())){
			material = Boolean.TRUE;
		} else if(modPacSolicCompraServicoVO.getDescricaoObjeto().equals(DominioObjetoDoPac.S.getDescricao())){
			material = Boolean.FALSE;
		}
		
		List<ScoEtapaModPac> listaEtapaMod = this.getScoEtapaModPacDAO().obterEtapasRelacionadasModalidade(modPacSolicCompraServicoVO, 
																											material, 
																											localPACVO, 
																											etapaVO);
		
		for (ScoEtapaModPac etapaMod : listaEtapaMod) {
			EtapasRelacionadasPacVO vo = new EtapasRelacionadasPacVO();
			vo.setDescricaoLocProcesso(etapaMod.getLocalizacaoProcesso().getDescricao());
			vo.setDescricaoEtapa(etapaMod.getDescricao());
			vo.setSituacao(DominioSituacaoEtapaPac.PD);
			vo.setTempoPrevisto(etapaMod.getNumeroDias() != null ? etapaMod.getNumeroDias() : 0);
			vo.setCodigoEtapa(etapaMod.getCodigo());
			
			
			listaVO.add(vo);
		}
		
	}

	// RN08 - #22068
	public void gravarNovaEtapa(ScoLocalizacaoProcesso localNovaEtapa, Integer numeroLicitacao, 
									String descricaoNovaEtapa, Short novaEtapaTempoPrevisto) throws ApplicationBusinessException {

		ScoEtapaPac etapa = this.getScoEtapaPacDAO().obterPorChavePrimaria(numeroLicitacao);
		
		if(etapa != null){
			if(etapa.getDescricaoEtapa().equalsIgnoreCase(descricaoNovaEtapa)){
				throw new ApplicationBusinessException(PlanejamentoAndamentoPacRNExceptionCode.ETAPA_EXISTENTE);
			}
		}
		
		ScoEtapaPac novaEtapa = new ScoEtapaPac();
		
		ScoLocalizacaoProcesso localizacaoProcesso = this.getScoLocalizacaoProcessoDAO().obterPorChavePrimaria(localNovaEtapa.getCodigo());
		novaEtapa.setLocalizacaoProcesso(localizacaoProcesso);
		
		ScoLicitacao licitacao = this.getScoLicitacaoDAO().obterPorChavePrimaria(numeroLicitacao);
		novaEtapa.setLicitacao(licitacao);
		
		novaEtapa.setDescricaoEtapa(descricaoNovaEtapa);
		novaEtapa.setSituacao(DominioSituacaoEtapaPac.PD);
		novaEtapa.setTempoPrevisto(novaEtapaTempoPrevisto);
		novaEtapa.setVersion(0);
		
		this.getScoEtapaPacDAO().persistir(novaEtapa);
		this.getScoEtapaPacDAO().flush();
		
	}
	
	// C12 - #22068
		public List<HistoricoLogEtapaPacVO> pesquisarHistoricoEtapa(Integer codigoEtapa){
			
			List<HistoricoLogEtapaPacVO> listaHistoricoEtapa = new ArrayList<HistoricoLogEtapaPacVO>();
			
			List<Object[]> resultQuery = this.getScoLogEtapaPacDAO().pesquisarHistoricoEtapa(codigoEtapa);
			
			for (Object[] obj : resultQuery) {
				HistoricoLogEtapaPacVO vo = new HistoricoLogEtapaPacVO();
				
				vo.setDescricaoLocProcesso(obj[0].toString());
				vo.setDescricaoEtapa(obj[1].toString());
				
				if(obj[2].toString().equalsIgnoreCase(DominioSituacaoEtapaPac.AT.toString())){
					vo.setSituacaoLog(DominioSituacaoEtapaPac.AT);	
				} else if(obj[2].toString().equalsIgnoreCase(DominioSituacaoEtapaPac.PD.toString())){
					vo.setSituacaoLog(DominioSituacaoEtapaPac.PD);	
				} else if(obj[2].toString().equalsIgnoreCase(DominioSituacaoEtapaPac.RJ.toString())){
					vo.setSituacaoLog(DominioSituacaoEtapaPac.RJ);
				} else if(obj[2].toString().equalsIgnoreCase(DominioSituacaoEtapaPac.RZ.toString())){
					vo.setSituacaoLog(DominioSituacaoEtapaPac.RZ);
				}
				
				vo.setTempoPrevisto(Short.parseShort(obj[3].toString()));
				
				vo.setApontamentoUsuario(obj[4] != null ? obj[4].toString() : "");
				vo.setNomePessoaFisica(obj[5].toString());
				vo.setDataApontamento((Date) obj[6]);
				vo.setDataAlteracao((Date) obj[7]);
				
				listaHistoricoEtapa.add(vo);
			}
			
			return listaHistoricoEtapa;
			
		}

	// RN09 - #22068
	public void atualizarEtapa(Integer codigoEtapa, DominioSituacaoEtapaPac situacaoEtapaAtualizar,
									Short tempoPrevistoAtualizar, String descricaoObsAtualizar) {

		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		ScoEtapaPac etapa = this.getScoEtapaPacDAO().obterPorChavePrimaria(codigoEtapa);
		
		this.inserirLogEtapaPac(codigoEtapa, etapa);
		
		etapa.setSituacao(situacaoEtapaAtualizar);
		etapa.setTempoPrevisto(tempoPrevistoAtualizar);
		etapa.setApontamentoUsuario(descricaoObsAtualizar);
		etapa.setServidor(servidorLogado);
		etapa.setVersion(etapa.getVersion());
		
		this.getScoEtapaPacDAO().atualizar(etapa);
		this.getScoEtapaPacDAO().flush();
	}
	
	private void inserirLogEtapaPac(Integer codigoEtapa, ScoEtapaPac etapa){
		ScoLogEtapaPac logEtapa = new ScoLogEtapaPac();
		
		logEtapa.setEtapa(etapa);
		logEtapa.setSituacao(etapa.getSituacao());
		logEtapa.setTempoPrevisto(etapa.getTempoPrevisto());
		logEtapa.setApontamentoUsuario(etapa.getApontamentoUsuario());
		logEtapa.setServidor(etapa.getServidor());
		logEtapa.setDataApontamento(etapa.getDataApontamento());
		logEtapa.setDataAlteracao(new Date());
		
		this.getScoLogEtapaPacDAO().persistir(logEtapa);
		this.getScoLogEtapaPacDAO().flush();
	}
	
	

		// RN09 - #22068
		public void atualizarEtapa(Integer codigoEtapa, DominioSituacaoEtapaPac situacaoEtapaAtualizar,
										Short tempoPrevistoAtualizar, String descricaoObsAtualizar, RapServidores servidorLogado) {

			ScoEtapaPac etapa = this.getScoEtapaPacDAO().obterPorChavePrimaria(codigoEtapa);
			
			this.inserirLogEtapaPac(codigoEtapa, etapa);
			
			etapa.setSituacao(situacaoEtapaAtualizar);
			etapa.setTempoPrevisto(tempoPrevistoAtualizar);
			etapa.setApontamentoUsuario(descricaoObsAtualizar);
			etapa.setServidor(servidorLogado);
			etapa.setVersion(etapa.getVersion());
			etapa.setDataApontamento(new Date());
			
			this.getScoEtapaPacDAO().atualizar(etapa);
			this.getScoEtapaPacDAO().flush();
		}
		
		

	
}
