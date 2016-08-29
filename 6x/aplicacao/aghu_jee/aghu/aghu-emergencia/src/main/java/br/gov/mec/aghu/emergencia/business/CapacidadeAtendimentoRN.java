package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamCapacidadeAtendDAO;
import br.gov.mec.aghu.emergencia.dao.MamCapacidadeAtendJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamEmgEspecialidadesDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.MamCapacidadeAtendVO;
import br.gov.mec.aghu.model.MamCapacidadeAtend;
import br.gov.mec.aghu.model.MamCapacidadeAtendJn;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
/**
 * @author israel.haas
 */
@Stateless
public class CapacidadeAtendimentoRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject
	private MamCapacidadeAtendDAO mamCapacidadeAtendDAO;
	
	@Inject
	private MamEmgEspecialidadesDAO mamEmgEspecialidadesDAO;
	
	@Inject
	private MamCapacidadeAtendJnDAO mamCapacidadeAtendJnDAO;
	
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void excluirCapacidadeAtend(MamCapacidadeAtendVO capacidadeSelecionada) {
		
		MamCapacidadeAtend capacidadeAtend = this.mamCapacidadeAtendDAO.obterPorChavePrimaria(capacidadeSelecionada.getSeq());
		MamCapacidadeAtend capacidadeAtendOriginal = this.mamCapacidadeAtendDAO.obterOriginal(capacidadeSelecionada.getSeq());
		
		inserirJournalCapacidadeAtends(capacidadeAtendOriginal, DominioOperacoesJournal.DEL);
		
		this.mamCapacidadeAtendDAO.remover(capacidadeAtend);
	}
	
	/**
	 * @param capacidadeAtendOriginal
	 * @param operacao
	 */
	public void inserirJournalCapacidadeAtends(MamCapacidadeAtend capacidadeAtendOriginal, DominioOperacoesJournal operacao) {
		
		MamCapacidadeAtendJn mamCapacidadeAtendJn = new MamCapacidadeAtendJn();
		
		mamCapacidadeAtendJn.setNomeUsuario(usuario.getLogin());
		mamCapacidadeAtendJn.setOperacao(operacao);
		mamCapacidadeAtendJn.setSeq(capacidadeAtendOriginal.getSeq());
		mamCapacidadeAtendJn.setEepEspSeq(capacidadeAtendOriginal.getMamEmgEspecialidades().getEspSeq());
		mamCapacidadeAtendJn.setQtdeInicialPac(capacidadeAtendOriginal.getQtdeInicialPac());
		mamCapacidadeAtendJn.setQtdeFinalPac(capacidadeAtendOriginal.getQtdeFinalPac());
		mamCapacidadeAtendJn.setCapacidadeAtend(capacidadeAtendOriginal.getCapacidadeAtend());
		mamCapacidadeAtendJn.setIndSituacao(capacidadeAtendOriginal.getIndSituacao());
		mamCapacidadeAtendJn.setCriadoEm(capacidadeAtendOriginal.getCriadoEm());
		mamCapacidadeAtendJn.setSerMatricula(capacidadeAtendOriginal.getSerMatricula());
		mamCapacidadeAtendJn.setSerVinCodigo(capacidadeAtendOriginal.getSerVinCodigo());
		
		this.mamCapacidadeAtendJnDAO.persistir(mamCapacidadeAtendJn);
	}
	
	public void inserirCapacidadeAtend(Short espSeq, Short qtdeInicialPac, Short qtdeFinalPac,
			Short capacidadeAtend, Boolean indSituacao) {
		
		MamCapacidadeAtend mamCapacidadeAtend = new MamCapacidadeAtend();
		mamCapacidadeAtend.setMamEmgEspecialidades(this.mamEmgEspecialidadesDAO.obterPorChavePrimaria(espSeq));
		mamCapacidadeAtend.setQtdeInicialPac(qtdeInicialPac);
		mamCapacidadeAtend.setQtdeFinalPac(qtdeFinalPac);
		mamCapacidadeAtend.setCapacidadeAtend(capacidadeAtend);
		mamCapacidadeAtend.setIndSituacao(DominioSituacao.getInstance(indSituacao));
		mamCapacidadeAtend.setCriadoEm(new Date());
		mamCapacidadeAtend.setSerMatricula(usuario.getMatricula());
		mamCapacidadeAtend.setSerVinCodigo(usuario.getVinculo());
		
		this.mamCapacidadeAtendDAO.persistir(mamCapacidadeAtend);
	}
	
	public void atualizarCapacidadeAtend(Integer capacidadeSeq, Short qtdeInicialPac, Short qtdeFinalPac,
			Short capacidadeAtend, Boolean indSituacao) {
		
		MamCapacidadeAtend capacidadeAtendOriginal = this.mamCapacidadeAtendDAO.obterOriginal(capacidadeSeq);
		
		MamCapacidadeAtend mamCapacidadeAtend = this.mamCapacidadeAtendDAO.obterPorChavePrimaria(capacidadeSeq);
		mamCapacidadeAtend.setQtdeInicialPac(qtdeInicialPac);
		mamCapacidadeAtend.setQtdeFinalPac(qtdeFinalPac);
		mamCapacidadeAtend.setCapacidadeAtend(capacidadeAtend);
		mamCapacidadeAtend.setIndSituacao(DominioSituacao.getInstance(indSituacao));
		mamCapacidadeAtend.setSerMatricula(usuario.getMatricula());
		mamCapacidadeAtend.setSerVinCodigo(usuario.getVinculo());
		
		this.mamCapacidadeAtendDAO.atualizar(mamCapacidadeAtend);
		
		this.inserirJournalCapacidadeAtends(capacidadeAtendOriginal, DominioOperacoesJournal.UPD);
	}
}
