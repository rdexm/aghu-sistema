package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalServidorDAO;
import br.gov.mec.aghu.dominio.DominioFormaIdentificacaoCaixaPostal;
import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.dominio.DominioTipoMensagemExame;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.MptProtocoloCuidados;
import br.gov.mec.aghu.model.MptProtocoloCuidadosDia;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentosDia;
import br.gov.mec.aghu.model.MptProtocoloSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MptProtocoloCuidadosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCuidadosDiaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloItemMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDiaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptVersaoProtocoloSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloSessaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MptProtocoloSessaoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MptProtocoloSessaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
		
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO;
		
	@Inject
	private MptProtocoloSessaoDAO mptProtocoloSessaoDAO;
	
	@Inject
	private MptVersaoProtocoloSessaoDAO mptVersaoProtocoloSessaoDAO;

	@Inject
	private MptProtocoloCuidadosDAO mptProtocoloCuidadosDAO;

	@Inject
	private MptProtocoloCuidadosDiaDAO mptProtocoloCuidadosDiaDAO;

	@Inject
	private MptProtocoloMedicamentosDiaDAO mptProtocoloMedicamentosDiaDAO;
	
	@Inject
	private AghCaixaPostalDAO aghCaixaPostalDAO;
	
	@Inject
	private AghCaixaPostalServidorDAO aghCaixaPostalServidorDAO;

	@Inject
	private MptProtocoloMedicamentosDAO mptProtocoloMedicamentosDAO;

	@Inject
	private MptProtocoloItemMedicamentosDAO mptProtocoloItemMedicamentosDAO;
		
	
	private static final long serialVersionUID = 2797194818319163103L;
	
	
	public MptVersaoProtocoloSessao inserirCopiaProtocolo(MptTipoSessao mptTipoSessao, String descricao, Integer qtdCiclo, Integer versao,
			List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloSolucoes, 
			List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloCuidados, Short diasTratamento) throws ApplicationBusinessException {
		
		MptProtocoloSessao mptProtocoloSessao = new MptProtocoloSessao();
		MptVersaoProtocoloSessao mptVersaoProtocoloSessao = new MptVersaoProtocoloSessao();
		MptVersaoProtocoloSessao versaoProtocoloSessaoNovo = new MptVersaoProtocoloSessao(); 
		
		//Inserir Protocolo Sessão.
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());		
		
		versaoProtocoloSessaoNovo = persistirProtocoloSessao(mptTipoSessao, descricao, qtdCiclo, versao,
				mptProtocoloSessao, mptVersaoProtocoloSessao, servidorLogado, true, diasTratamento);
		
		inserirMedicamentosSolucoesCuidados(listaProtocoloMedicamentosVO,
				listaProtocoloSolucoes, listaProtocoloCuidados,
				mptVersaoProtocoloSessao, servidorLogado);
		return versaoProtocoloSessaoNovo;
				
	}
	
	public MptVersaoProtocoloSessao inserirMptProtocoloSessao(MptTipoSessao mptTipoSessao, String descricao, Integer qtdCiclo, Integer versao, Short diasTratamento) throws ApplicationBusinessException {
		
		MptProtocoloSessao mptProtocoloSessao = new MptProtocoloSessao();
		MptVersaoProtocoloSessao mptVersaoProtocoloSessao = new MptVersaoProtocoloSessao();
		MptVersaoProtocoloSessao versaoProtocoloSessaoNovo = new MptVersaoProtocoloSessao(); 
		
		//Inserir Protocolo Sessão.
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());		
		versaoProtocoloSessaoNovo = persistirProtocoloSessao(mptTipoSessao, descricao, qtdCiclo, versao,
				mptProtocoloSessao, mptVersaoProtocoloSessao, servidorLogado, false, diasTratamento);
		return versaoProtocoloSessaoNovo;
	}
	
	private MptVersaoProtocoloSessao persistirProtocoloSessao(MptTipoSessao mptTipoSessao,
			String descricao, Integer qtdCiclo, Integer versao,
			MptProtocoloSessao mptProtocoloSessao,
			MptVersaoProtocoloSessao mptVersaoProtocoloSessao,
			RapServidores servidorLogado, boolean isCopiaProtocolo, Short diasTratamento) {
		mptProtocoloSessao.setServidor(servidorLogado);		
		mptProtocoloSessao.setCriadoEm(new Date());
		mptProtocoloSessao.setTitulo(descricao);		
		MptTipoSessao tipoSessaoOriginal = mptTipoSessaoDAO.obterPorChavePrimaria(mptTipoSessao.getSeq());		
		mptProtocoloSessao.setTipoSessao(tipoSessaoOriginal);			
		if (!isCopiaProtocolo) {
			mptProtocoloSessao.setQtdCiclo(qtdCiclo);
		}
		mptProtocoloSessaoDAO.persistir(mptProtocoloSessao);		
		this.flush();
		
		//Inserir Versão Protocolo Sessão.
		mptProtocoloSessao = mptProtocoloSessaoDAO.obterPorChavePrimaria(mptProtocoloSessao.getSeq());		
		mptVersaoProtocoloSessao.setProtocoloSessao(mptProtocoloSessao);
		mptVersaoProtocoloSessao.setVersao(versao);
		mptVersaoProtocoloSessao.setCriadoEm(new Date());
		mptVersaoProtocoloSessao.setServidor(servidorLogado);
		mptVersaoProtocoloSessao.setServidorResponsavel(servidorLogado);
		mptVersaoProtocoloSessao.setIndSituacao(DominioSituacaoProtocolo.C);
		if (isCopiaProtocolo) {
			mptVersaoProtocoloSessao.setQtdCiclos(qtdCiclo);
		}
		if (diasTratamento !=  null) {
			mptVersaoProtocoloSessao.setDiasTratamento(diasTratamento);
		}
		mptVersaoProtocoloSessaoDAO.persistir(mptVersaoProtocoloSessao);
		this.flush();
		
		return mptVersaoProtocoloSessao;
	}

	private void inserirMedicamentosSolucoesCuidados(
			List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO,
			List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloSolucoes,
			List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloCuidados,
			MptVersaoProtocoloSessao mptVersaoProtocoloSessao,
			RapServidores servidorLogado) throws ApplicationBusinessException {
		//Inserir Medicamento e seus itens.
		for (ProtocoloMedicamentoSolucaoCuidadoVO itemMedicamento : listaProtocoloMedicamentosVO) {	
			MptProtocoloMedicamentos ptmOriginal = mptProtocoloMedicamentosDAO.obterPorChavePrimaria(itemMedicamento.getPtmSeq());
			mptProtocoloMedicamentosDAO.desatachar(ptmOriginal);
			inserirMedicamento(ptmOriginal, mptVersaoProtocoloSessao, servidorLogado);
		}
		
		//Inserir Cuidados.
		for (ProtocoloMedicamentoSolucaoCuidadoVO itemCuidado : listaProtocoloCuidados) {
			MptProtocoloCuidados cuidOriginal = mptProtocoloCuidadosDAO.obterPorChavePrimaria(itemCuidado.getPcuSeq());
			mptProtocoloCuidadosDAO.desatachar(cuidOriginal);
			inserirCuidados(itemCuidado, cuidOriginal, mptVersaoProtocoloSessao, servidorLogado);
		}
		
		//Inserir Solução.
		for (ProtocoloMedicamentoSolucaoCuidadoVO itemSolucao : listaProtocoloSolucoes) {
			MptProtocoloMedicamentos solucaoOriginal = mptProtocoloMedicamentosDAO.obterPorChavePrimaria(itemSolucao.getPtmSeq());	
			mptProtocoloMedicamentosDAO.desatachar(solucaoOriginal);
			inserirSolucao(solucaoOriginal, mptVersaoProtocoloSessao, servidorLogado);
		}
	}
	
	/**
	 * Inseri os Medicamentos.
	 * @param mptProtocoloMedicamentos
	 * @param seqVps
	 * @throws ApplicationBusinessException
	 */
	private void inserirMedicamento(MptProtocoloMedicamentos ptmOriginal, MptVersaoProtocoloSessao seqVps, RapServidores servidorLogado) throws ApplicationBusinessException {		
		Long seqMedicamento = ptmOriginal.getSeq();
		//Persiste Medicamento.
		ptmOriginal.setServidor(servidorLogado);
		ptmOriginal.setVersaoProtocoloSessao(seqVps);		
		ptmOriginal.setCriadoEm(new Date());				
		ptmOriginal.setSeq(null);
		this.mptProtocoloMedicamentosDAO.persistir(ptmOriginal);
		this.flush();
		//Persiste item de medicamento.
		List<MptProtocoloItemMedicamentos> mptProtocoloItemMedicamentos = mptProtocoloItemMedicamentosDAO.obterItensMedicamentoPorSeqProtocolo(seqMedicamento);
		if (mptProtocoloItemMedicamentos != null && !mptProtocoloItemMedicamentos.isEmpty()) {
			for (MptProtocoloItemMedicamentos ptimOriginal : mptProtocoloItemMedicamentos) {
				mptProtocoloItemMedicamentosDAO.desatachar(ptimOriginal);
				ptimOriginal.setProtocoloMedicamentos(ptmOriginal);
				ptimOriginal.setServidor(servidorLogado);
				ptimOriginal.setCriadoEm(new Date());
				ptimOriginal.setServidor(servidorLogado);
				ptimOriginal.setSeq(null);
				this.mptProtocoloItemMedicamentosDAO.persistir(ptimOriginal);
			}
		}
		
		List<MptProtocoloMedicamentosDia> diasSolucao = mptProtocoloMedicamentosDiaDAO.verificarExisteDiaMarcadoParaProtocolo(seqMedicamento);
		if (diasSolucao != null && !diasSolucao.isEmpty()){
			for (MptProtocoloMedicamentosDia mptProtocoloMedicamentoDia : diasSolucao) {
				mptProtocoloMedicamentosDiaDAO.desatachar(mptProtocoloMedicamentoDia);
				mptProtocoloMedicamentoDia.setCriadoEm(new Date());
				mptProtocoloMedicamentoDia.setServidor(servidorLogado);
				mptProtocoloMedicamentoDia.setProtocoloMedicamentos(ptmOriginal);
				mptProtocoloMedicamentoDia.setVersaoProtocoloSessao(seqVps);
				mptProtocoloMedicamentoDia.setSeq(null);
				mptProtocoloMedicamentosDiaDAO.persistir(mptProtocoloMedicamentoDia);
			}			
		}
	}

	/**
	 * Inseri os Cuidados.
	 * @param MptProtocoloCuidados
	 * @throws ApplicationBusinessException
	 */
	private void inserirCuidados(ProtocoloMedicamentoSolucaoCuidadoVO itemCuidado, MptProtocoloCuidados cuidOriginal,MptVersaoProtocoloSessao seqVps, RapServidores servidorLogado) throws ApplicationBusinessException {
		//Persiste Medicamento Cuidado.
		Integer seqProtocoloCuidado = cuidOriginal.getSeq();
		cuidOriginal.setVersaoProtocoloSessao(seqVps);
		cuidOriginal.setCriadoEm(new Date());
		cuidOriginal.setRapServidores(servidorLogado);		
		cuidOriginal.setSeq(null);
		this.mptProtocoloCuidadosDAO.persistir(cuidOriginal);
		
		List<MptProtocoloCuidadosDia> diasCuidados = mptProtocoloCuidadosDiaDAO.verificarDiaCuidado(seqProtocoloCuidado);
		if (diasCuidados!= null && !diasCuidados.isEmpty()){
			for (MptProtocoloCuidadosDia mptProtocoloCuidadosDia : diasCuidados) {
				mptProtocoloCuidadosDiaDAO.desatachar(mptProtocoloCuidadosDia);
				mptProtocoloCuidadosDia.setCriadoEm(new Date());
				mptProtocoloCuidadosDia.setServidor(servidorLogado);
				mptProtocoloCuidadosDia.setProtocoloCuidados(cuidOriginal);
				mptProtocoloCuidadosDia.setVersaoProtocoloSessao(seqVps);
				mptProtocoloCuidadosDia.setSeq(null);
				mptProtocoloCuidadosDiaDAO.persistir(mptProtocoloCuidadosDia);
			}			
		}
	}

	/**
	 * Inseri as Soluções.
	 * @param solucao
	 * @throws ApplicationBusinessException
	 */
	private void inserirSolucao(MptProtocoloMedicamentos solucao, MptVersaoProtocoloSessao seqVps, RapServidores servidorLogado) throws ApplicationBusinessException {
		
		//Persiste Solução.
		Long seqSolucao = solucao.getSeq();
		solucao.setSeq(null);
		solucao.setCriadoEm(new Date());
		solucao.setVersaoProtocoloSessao(seqVps);
		solucao.setServidor(servidorLogado);
		this.mptProtocoloMedicamentosDAO.persistir(solucao);
		this.flush();
		List<MptProtocoloItemMedicamentos> listaItensSolucoes = mptProtocoloItemMedicamentosDAO.obterItensMedicamentoPorSeqProtocolo(seqSolucao);
		if (listaItensSolucoes != null && !listaItensSolucoes.isEmpty()){
			//Persiste Itens da Solução.
			for (MptProtocoloItemMedicamentos itemSolucoes : listaItensSolucoes) {			 
				mptProtocoloItemMedicamentosDAO.desatachar(itemSolucoes);
				itemSolucoes.setSeq(null);
				itemSolucoes.setServidor(servidorLogado);
				itemSolucoes.setCriadoEm(new Date());
				itemSolucoes.setProtocoloMedicamentos(solucao);
				this.mptProtocoloItemMedicamentosDAO.persistir(itemSolucoes);			
			}			
		}
		
		List<MptProtocoloMedicamentosDia> diasSolucao = mptProtocoloMedicamentosDiaDAO.verificarExisteDiaMarcadoParaProtocolo(seqSolucao);
		if (diasSolucao != null && !diasSolucao.isEmpty()){
			for (MptProtocoloMedicamentosDia mptProtocoloMedicamentoDia : diasSolucao) {
				this.mptProtocoloMedicamentosDiaDAO.desatachar(mptProtocoloMedicamentoDia);
				mptProtocoloMedicamentoDia.setCriadoEm(new Date());
				mptProtocoloMedicamentoDia.setServidor(servidorLogado);
				mptProtocoloMedicamentoDia.setProtocoloMedicamentos(solucao);
				mptProtocoloMedicamentoDia.setVersaoProtocoloSessao(seqVps);
				mptProtocoloMedicamentoDia.setSeq(null);
				this.mptProtocoloMedicamentosDiaDAO.persistir(mptProtocoloMedicamentoDia);
			}			
		}
	}
	
	
	
	public void atualizarProtocolo(MptTipoSessao mptTipoSessao, String descricao, Integer qtdCiclo, 
			Integer versao, Integer seqProtocolo, DominioSituacaoProtocolo situacaoProtocolo, Integer seqVersaoProtocoloSessao)throws ApplicationBusinessException{
		
		MptProtocoloSessao mptProtocoloSessao = new MptProtocoloSessao();
		MptVersaoProtocoloSessao mptVersaoProtocoloSessao = new MptVersaoProtocoloSessao();
		
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		mptVersaoProtocoloSessao = mptVersaoProtocoloSessaoDAO.verificaSituacaoProtocoloPorSeq(seqVersaoProtocoloSessao);
		
		mptProtocoloSessao = mptProtocoloSessaoDAO.obterPorChavePrimaria(seqProtocolo);
		mptProtocoloSessao.setTitulo(descricao);
		mptProtocoloSessao.setQtdCiclo(qtdCiclo);
		mptProtocoloSessao.setTipoSessao(mptTipoSessao);
		
		if(situacaoProtocolo != DominioSituacaoProtocolo.A){
			mptProtocoloSessao.setServidor(servidorLogado);
			mptVersaoProtocoloSessao.setIndSituacao(situacaoProtocolo);
		}
		mptProtocoloSessaoDAO.atualizar(mptProtocoloSessao);
				
		mptVersaoProtocoloSessao.setVersao(versao);
		mptVersaoProtocoloSessao.setServidor(servidorLogado);
		mptVersaoProtocoloSessao.setServidorResponsavel(servidorLogado);
		
		mptVersaoProtocoloSessaoDAO.atualizar(mptVersaoProtocoloSessao);
		
		if(situacaoProtocolo == DominioSituacaoProtocolo.L || situacaoProtocolo == DominioSituacaoProtocolo.I){
			inserirCaixaPostal(mptProtocoloSessao, mptVersaoProtocoloSessao);
		}
	}
	
	public void inserirCaixaPostal(MptProtocoloSessao mptProtocoloSessao, MptVersaoProtocoloSessao mptVersaoProtocoloSessao){
		String mensagem = null;
		AghCaixaPostal aghCaixaPostal = new AghCaixaPostal();
		AghCaixaPostalServidor aghCaixaPostalServidor = new AghCaixaPostalServidor();
		
		aghCaixaPostal.setDthrInicio(new Date());
		aghCaixaPostal.setCriadoEm(new Date());
		aghCaixaPostal.setTipoMensagem(DominioTipoMensagemExame.A);
		aghCaixaPostal.setAcaoObrigatoria(false);
		aghCaixaPostal.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.E);
		
		
		if(mptVersaoProtocoloSessao.getIndSituacao() == DominioSituacaoProtocolo.L){
			mensagem = "O protocolo ".concat(mptProtocoloSessao.getTitulo()).concat(" passou para a situa\u00E7\u00E3o liberado.");
			aghCaixaPostal.setMensagem(mensagem);
		}else{
			mensagem = "O protocolo ".concat(mptProtocoloSessao.getTitulo()).concat(" passou para a situa\u00E7\u00E3o inativo.");
			aghCaixaPostal.setMensagem(mensagem);
		}
			aghCaixaPostalDAO.persistir(aghCaixaPostal);
		
		MptProtocoloSessao mptProtocolo = this.mptProtocoloSessaoDAO.obterProtocoloServidorResponsavel(mptProtocoloSessao.getSeq());
		if(mptProtocolo != null){
			AghCaixaPostalServidorId id = new AghCaixaPostalServidorId(aghCaixaPostal.getSeq(), mptProtocolo.getServidor());
			aghCaixaPostalServidor.setId(id);
			aghCaixaPostalServidor.setSituacao(DominioSituacaoCxtPostalServidor.N);
			
			aghCaixaPostalServidorDAO.persistir(aghCaixaPostalServidor);
		}
	}
	
	public List<DominioSituacaoProtocolo> verificarSituacaoProtocolo(Integer seqProtocolo){
		List<DominioSituacaoProtocolo> listaSituacaoProtocolo = new ArrayList<DominioSituacaoProtocolo>();
				
		MptVersaoProtocoloSessao mptVersaoProtocoloSessao = this.mptVersaoProtocoloSessaoDAO.verificaSituacaoProtocoloPorSeq(seqProtocolo);
		if(mptVersaoProtocoloSessao != null){
			if(mptVersaoProtocoloSessao.getIndSituacao() == DominioSituacaoProtocolo.C){
				listaSituacaoProtocolo.add(DominioSituacaoProtocolo.C);
				listaSituacaoProtocolo.add(DominioSituacaoProtocolo.H);
				listaSituacaoProtocolo.add(DominioSituacaoProtocolo.I);
				
			}else if(mptVersaoProtocoloSessao.getIndSituacao() == DominioSituacaoProtocolo.H){
				listaSituacaoProtocolo.add(DominioSituacaoProtocolo.H);
				listaSituacaoProtocolo.add(DominioSituacaoProtocolo.L);
				listaSituacaoProtocolo.add(DominioSituacaoProtocolo.I);
			
			}else if(mptVersaoProtocoloSessao.getIndSituacao() == DominioSituacaoProtocolo.L){
				listaSituacaoProtocolo.add(DominioSituacaoProtocolo.L);
				listaSituacaoProtocolo.add(DominioSituacaoProtocolo.I);
			}else if(mptVersaoProtocoloSessao.getIndSituacao() == DominioSituacaoProtocolo.I){
				listaSituacaoProtocolo.add(DominioSituacaoProtocolo.I);
			}
		}
				
		return listaSituacaoProtocolo;
	}
	
	public MptVersaoProtocoloSessao inserirNovaVersaoProtocoloSessao(Integer seqProtocolo, Integer versao, Integer qtdCiclos, DominioSituacaoProtocolo indSituacao, 
			List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloMedicamentosVO, List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloSolucoes,
			List<ProtocoloMedicamentoSolucaoCuidadoVO> listaProtocoloCuidados, Short diasTratamento) throws ApplicationBusinessException{
		MptProtocoloSessao mptProtocoloSessao = new MptProtocoloSessao();
		MptVersaoProtocoloSessao mptVersaoProtocoloSessao = new MptVersaoProtocoloSessao();
		
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		mptProtocoloSessao = mptProtocoloSessaoDAO.obterPorChavePrimaria(seqProtocolo);
		
		mptVersaoProtocoloSessao.setProtocoloSessao(mptProtocoloSessao);
		mptVersaoProtocoloSessao.setVersao(versao);
		mptVersaoProtocoloSessao.setCriadoEm(new Date());
		mptVersaoProtocoloSessao.setServidor(servidorLogado);
		mptVersaoProtocoloSessao.setServidorResponsavel(servidorLogado);
		mptVersaoProtocoloSessao.setQtdCiclos(qtdCiclos);
		mptVersaoProtocoloSessao.setDiasTratamento(diasTratamento);
		
		if(indSituacao == null){
			mptVersaoProtocoloSessao.setIndSituacao(DominioSituacaoProtocolo.C);
		}else{
			mptVersaoProtocoloSessao.setIndSituacao(indSituacao);
		}
		
		mptVersaoProtocoloSessaoDAO.persistir(mptVersaoProtocoloSessao);
		this.flush();
		
		inserirMedicamentosSolucoesCuidados(listaProtocoloMedicamentosVO, listaProtocoloSolucoes, listaProtocoloCuidados, mptVersaoProtocoloSessao, servidorLogado);
		
		return mptVersaoProtocoloSessao;
	}
	
	public MptProtocoloSessao obterMptProtocoloSessaoPorSeq(Integer seqProtocolo) {
		MptProtocoloSessao mptProtocoloSessao = mptProtocoloSessaoDAO.obterProtocoloPorSeq(seqProtocolo);
		return mptProtocoloSessao;
	}

	public ProtocoloSessaoVO obterItemVersaoProtocolo(Integer seqVersaoProtocolo) {
		ProtocoloSessaoVO protocoloSessao = mptProtocoloSessaoDAO.obterItemVersaoProtocolo(seqVersaoProtocolo);
		return protocoloSessao;
	}
	

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public MptTipoSessaoDAO getMptTipoSessaoDAO() {
		return mptTipoSessaoDAO;
	}
	

}
