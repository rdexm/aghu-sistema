package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoContaApac;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaApacJnDAO;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatItemContaApacJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FatItemContaApacRN extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(FatItemContaApacRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatItemContaApacDAO fatItemContaApacDAO;

	@Inject
	private FatItemContaApacJnDAO fatItemContaApacJnDAO;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	protected ContaHospitalarRN contaHospitalarRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1618901598298885545L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private enum FatItemContaApacRNExceptionCode implements BusinessExceptionCode {
		;
	}

	public void persistir(final FatItemContaApac newItemCtaApac, final FatItemContaApac oldItemCtaApac, final Boolean flush) throws BaseException {
		if (newItemCtaApac != null) {
			if (newItemCtaApac.getId() == null) {
				this.inserir(newItemCtaApac, flush);
			} else {
				this.atualizar(newItemCtaApac, oldItemCtaApac, flush);
			}
		}
	}

	public void inserir(final FatItemContaApac newItemCtaApac, final Boolean flush) {
		// TODO implementar triggers *** Fora do escopo de Encerramento de Ambulatório
		getFatItemContaApacDAO().persistir(newItemCtaApac);
		if (flush){
			getFatItemContaApacDAO().flush();
		}
	}

	/** Estória de Usuário #40230
	 * ORADB: Trigger FATT_ICA_ARU<br/>
	 * Implementada pelo método inserirJournal(), com parâmetro "DominioOperacoesJournal.UPD"
	 * @author marcelo.deus
	 * @throws BaseException 
	 */
	public void atualizar(final FatItemContaApac newItemCtaApac, final FatItemContaApac oldItemCtaApac, final Boolean flush) throws BaseException {
		verificarConvenioPlano(newItemCtaApac, oldItemCtaApac);
		verificarItemSolExame(newItemCtaApac, oldItemCtaApac);
		newItemCtaApac.setAlteradoEm(new Date());
		newItemCtaApac.setAlteradoPor(getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getUsuario());
		this.getFatItemContaApacDAO().atualizar(newItemCtaApac);
		
		/** Método que executa a operação da Trigger FATT_ICA_ARU
		 */
		if(habilitadoParaJornalling(newItemCtaApac, oldItemCtaApac)){
			this.inserirJournal(oldItemCtaApac, DominioOperacoesJournal.UPD);
		}
		/** Fim da chamada do Método que executa a operação da Trigger FATT_ICA_ARU
		 */
		
		if (flush){
			getFatItemContaApacDAO().flush();
		}
	}

	
	public void remover(final FatItemContaApac elemento) throws ApplicationBusinessException {
		this.getFatItemContaApacDAO().remover(elemento);
		inserirJournal(elemento, DominioOperacoesJournal.DEL);
	}

	/**
	 * Realiza a inserção na tabela de journalling da FatItemContaApac, conforme
	 * a regra FATT_ICA_ARD.
	 * 
	 * @param fatItemContaApac
	 *            - Item de Conta modificado.
	 * @param operacao
	 *            - Tipo da operação
	 */
	private void inserirJournal(FatItemContaApac fatItemContaApac,
			DominioOperacoesJournal operacao) {

		RapServidores servidorLogado = getServidorLogadoFacade()
				.obterServidorLogado();

		FatItemContaApacJn journal = new FatItemContaApacJn();

		journal.setJnUser(servidorLogado.getUsuario());
		journal.setJnDateTime(new Date());
		journal.setJnOperation(operacao.toString());

		journal.setCapAtmNumero(
				fatItemContaApac.getId().getCapAtmNumero());
		journal.setCapSeqp(fatItemContaApac.getId().getCapSeqp());
		journal.setSeqp(fatItemContaApac.getId().getSeqp());
		journal.setQuantidade(fatItemContaApac.getQuantidade());
		journal.setIndSituacao(
				fatItemContaApac.getIndSituacao().toString());

		Character localCobranca = null;
		if (fatItemContaApac.getLocalCobranca() != null
				&& !fatItemContaApac.getLocalCobranca().equals("")) {
			localCobranca = fatItemContaApac.getLocalCobranca().charAt(0);
		}
		journal.setLocalCobranca(localCobranca);
		
		journal.setCriadoPor(fatItemContaApac.getCriadoPor());
		journal.setCriadoEm(fatItemContaApac.getCriadoEm());
		journal.setAlteradoPor(fatItemContaApac.getAlteradoPor());
		journal.setAlteradoEm(fatItemContaApac.getAlteradoEm());
		journal.setValor(fatItemContaApac.getValor());
		journal.setPhiSeq(fatItemContaApac.getProcedimentoHospitalarInterno().getSeq());
		journal.setSerMatricula(fatItemContaApac.getSerMatricula());
		journal.setSerVinCodigo(fatItemContaApac.getSerVinCodigo());
		journal.setIseSeqp(fatItemContaApac.getIseSeqp());
		journal.setIseSoeSeq(fatItemContaApac.getIseSoeSeq());
		journal.setPrhConNumero(fatItemContaApac.getPrhConNumero());
		journal.setPrhPhiSeq(fatItemContaApac.getPrhPhiSeq());
		journal.setDthrRealizado(fatItemContaApac.getDthrRealizado());
		journal.setPpcCrgSeq(fatItemContaApac.getPpcCrgSeq());
		journal.setPpcEprPciSeq(fatItemContaApac.getPpcEprPciSeq());
		journal.setPpcEprEspSeq(fatItemContaApac.getPpcEprEspSeq());
		journal.setPpcIndRespProc(fatItemContaApac.getPpcIndRespProc());
		journal.setPmrSeq(fatItemContaApac.getProcedimentoAmbRealizado().getSeq());

		getFatItemContaApacJnDAO().persistir(journal);
	}
	
	/** Estória de Usuário #40230
	 * ORADB: Trigger FATT_ICA_BRU<br/>
	 * @author marcelo.deus
	 * @throws BaseException 
	 */
	public void verificarConvenioPlano(final FatItemContaApac newItemCtaApac, final FatItemContaApac oldItemCtaApac) throws BaseException{
		
	 	if(newItemCtaApac.getId().getCapAtmNumero() != oldItemCtaApac.getId().getCapAtmNumero() ||
			newItemCtaApac.getProcedimentoAmbRealizado().getSeq() != oldItemCtaApac.getProcedimentoAmbRealizado().getSeq() ||
			newItemCtaApac.getProcedimentoHospitalarInterno().getSeq() != oldItemCtaApac.getProcedimentoHospitalarInterno().getSeq() ||
			!newItemCtaApac.getIndSituacao().equals(DominioSituacaoContaApac.C)){
	 		getContaHospitalarRN().verificarFiltroConvPlano(
				null, 
				newItemCtaApac.getId().getCapAtmNumero(),
				newItemCtaApac.getProcedimentoAmbRealizado().getSeq(), 
				null, 
				null, 
				newItemCtaApac.getProcedimentoHospitalarInterno().getSeq(), 
				null, 
				null, 
				null);
	 	}
 	}

	/** Estória de Usuário #40230
	 * ORADB: Trigger FATT_ICA_BASE_BRU<br/>
	 * @author marcelo.deus
	 * @throws BaseException 
	 */
	public void verificarItemSolExame(final FatItemContaApac newItemCtaApac, final FatItemContaApac oldItemCtaApac) throws BaseException{
		if(newItemCtaApac.getIseSoeSeq() != oldItemCtaApac.getIseSoeSeq() ||
				newItemCtaApac.getIseSeqp() != oldItemCtaApac.getIseSeqp()){
			getAelItemSolicitacaoExameDAO().verificaItemSolicitacaoExames(
					newItemCtaApac.getIseSoeSeq(), 
					newItemCtaApac.getIseSeqp(), 
					getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()).getUsuario());
		}
	}


	public boolean habilitadoParaJornalling(final FatItemContaApac newItemCtaApac, final FatItemContaApac oldItemCtaApac){
		if(newItemCtaApac.getId().getCapAtmNumero() != oldItemCtaApac.getId().getCapAtmNumero() ||
				newItemCtaApac.getId().getCapSeqp() != oldItemCtaApac.getId().getCapSeqp() ||
				newItemCtaApac.getId().getSeqp() != oldItemCtaApac.getId().getSeqp() ||
				newItemCtaApac.getProcedimentoAmbRealizado().getSeq() != oldItemCtaApac.getProcedimentoAmbRealizado().getSeq() ||
				newItemCtaApac.getProcedimentoHospitalarInterno().getSeq() != oldItemCtaApac.getProcedimentoHospitalarInterno().getSeq() ||
				newItemCtaApac.getIseSoeSeq() != oldItemCtaApac.getIseSoeSeq() ||
				newItemCtaApac.getIseSeqp() != oldItemCtaApac.getIseSeqp() ||
				newItemCtaApac.getAlteradoEm() != oldItemCtaApac.getAlteradoEm() ||
				newItemCtaApac.getAlteradoPor() != oldItemCtaApac.getAlteradoPor() ||
				newItemCtaApac.getCriadoEm() != oldItemCtaApac.getCriadoEm() ||
				newItemCtaApac.getCriadoPor() != oldItemCtaApac.getCriadoPor() ||
				newItemCtaApac.getDthrRealizado() != oldItemCtaApac.getDthrRealizado() ||
				newItemCtaApac.getIndSituacao() != oldItemCtaApac.getIndSituacao() ||
				newItemCtaApac.getLocalCobranca() != oldItemCtaApac.getLocalCobranca() ||
				newItemCtaApac.getPpcCrgSeq() != oldItemCtaApac.getPpcCrgSeq() ||
				newItemCtaApac.getPpcEprEspSeq() != oldItemCtaApac.getPpcEprEspSeq() ||
				newItemCtaApac.getPpcEprPciSeq() != oldItemCtaApac.getPpcEprPciSeq() ||
				newItemCtaApac.getPpcIndRespProc() != oldItemCtaApac.getPpcIndRespProc() ||
				newItemCtaApac.getPrhConNumero() != oldItemCtaApac.getPrhConNumero() ||
				newItemCtaApac.getPrhPhiSeq() != oldItemCtaApac.getPrhPhiSeq() ||
				newItemCtaApac.getQuantidade() != oldItemCtaApac.getQuantidade() ||
				newItemCtaApac.getSerMatricula() != oldItemCtaApac.getSerMatricula() ||
				newItemCtaApac.getSerVinCodigo() != oldItemCtaApac.getSerVinCodigo() ||
				newItemCtaApac.getValor() != oldItemCtaApac.getValor()){
			return true;
		} else {
			return false;
		}
	}

	protected FatItemContaApacDAO getFatItemContaApacDAO() {
		return fatItemContaApacDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	protected FatItemContaApacJnDAO getFatItemContaApacJnDAO() {
		return fatItemContaApacJnDAO;
	}

	public ContaHospitalarRN getContaHospitalarRN() {
		return contaHospitalarRN;
	}

	public AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
}
